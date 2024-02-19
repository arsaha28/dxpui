package com.dxp.ui.application.views.travelai;

import com.dxp.ui.application.views.MainLayout;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@PageTitle("TravelAI")
@Route(value = "chat", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TravelAIView extends HorizontalLayout {


    @Autowired
    private RestTemplate restTemplate;

    ResponseEntity<List> response;
    private ChatInfo[] chats = new ChatInfo[]{new ChatInfo("general", 0), new ChatInfo("support", 0),
            new ChatInfo("casual", 0)};
    private ChatInfo currentChat = chats[0];
    private Tabs tabs;

    public TravelAIView() {
        addClassNames("travel-ai-view", Width.FULL, Display.FLEX, Flex.AUTO);
        setSpacing(false);
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "DXP Agent");

        tabs = new Tabs();
        for (ChatInfo chat : chats) {
            MessageManager mm = new MessageManager(this, userInfo, chat.getCollaborationTopic());
            mm.setMessageHandler(context -> {
                if (currentChat != chat) {
                    chat.incrementUnread();
                }
            });

            tabs.add(createTab(chat));
        }
        tabs.setOrientation(Orientation.VERTICAL);
        tabs.addClassNames(Flex.GROW, Flex.SHRINK, Overflow.HIDDEN);

        CollaborationMessageList list = new CollaborationMessageList(userInfo, currentChat.getCollaborationTopic());
        list.setSizeFull();

        CollaborationMessageInput input = new CollaborationMessageInput(list);
        input.setWidthFull();
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = new Upload(memoryBuffer);
        singleFileUpload.setDropAllowed(false);
        singleFileUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            File targetFile = new File("src/main/resources/"+fileName);
            String serverUrl = "http://localhost:8081/wishupload";
            try {
                FileUtils.copyInputStreamToFile(fileData, targetFile);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                FileSystemResource fileSystemResource = new FileSystemResource(targetFile);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", fileSystemResource);
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                response = restTemplate
                        .postForEntity(serverUrl,requestEntity, List.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button button = new Button("Locate & Plan");
        button.setEnabled(false);

        Button flights = new Button("Search Flights");
        flights.setEnabled(false);
        flights.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);

        Button hotels = new Button("Search hotels");
        hotels.setEnabled(false);

        Dialog dialog = new Dialog();
        dialog.setWidth("1000");
        dialog.setWidth("1000");
        dialog.setHeaderTitle("Check Loan Eligibility");

        TextField age = new TextField("Age");
        TextField income = new TextField("Income");
        TextField housingExpenses = new TextField("Housing Expense");

        TextField foodExpenses = new TextField("Food Expense");
        TextField creditScore = new TextField("Credit Score");

        FormLayout formLayout = new FormLayout(age,income,housingExpenses,foodExpenses,creditScore);
        dialog.add(formLayout);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(age, 1);

        Button saveButton = new Button("Check", e -> {
            ResponseEntity<String> loanEntity = restTemplate
                    .getForEntity("http://localhost:8081/loan?age="+age.getValue()+"&income="+income.getValue()+"&housingExpenses="+housingExpenses.getValue()+"&foodExpenses="+foodExpenses.getValue()+"&creditScore="+creditScore.getValue(),String.class);
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

            Div text = new Div(
                    new Text("Sorry,You are not eligible for a Loan,create a Travel Saving POT instead")

            );

            Button closeButton = new Button(new Icon("lumo", "cross"));
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.setAriaLabel("Close");
            closeButton.addClickListener(event -> {
                notification.close();
            });

            HorizontalLayout layout = new HorizontalLayout(text, closeButton);
            layout.setAlignItems(Alignment.CENTER);

            notification.add(layout);
            notification.open();
            System.out.println(loanEntity.getBody());
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);


        Button eligibility = new Button("Eligibility", e -> dialog.open());
        eligibility.setEnabled(true);



        list.setSubmitter(activationContext -> {
            button.setEnabled(true);
            Registration registration = button.addClickListener(
                    event -> {
                        activationContext.appendMessage("Looks like you want to travel to " + response.getBody().toString());
                        activationContext.appendMessage("Let me help with an approximate budget......");
                        ResponseEntity<String> budgetEntity = restTemplate
                                .getForEntity("http://localhost:8081/budget?familyMemberCount=1&numberOfDays=3&travelLocation="+response.getBody().get(0),String.class);
                        activationContext.appendMessage(budgetEntity.getBody());

                        flights.setEnabled(true);
                    }
            );
            Registration flightRegistration = flights.addClickListener(
                    event -> {
                        ResponseEntity<List> flightEntity = restTemplate
                                .getForEntity("http://localhost:8081/flight?departure=LHR&arrival="+String.valueOf(response.getBody().get(1)).strip()+"&outBoundDate=2024-02-12&returnDate=2024-02-17",List.class);
                        flightEntity.getBody().stream().forEach(s->activationContext.appendMessage(s.toString()));
                        hotels.setEnabled(true);
                    }
            );
            Registration hotelRegistration = hotels.addClickListener(
                    event -> {
                        ResponseEntity<List> hotelsEntity = restTemplate
                                .getForEntity("http://localhost:8081/hotel?location="+String.valueOf(response.getBody().get(0)).strip()+"&checkInDate=2024-02-12&checkOutDate=2024-02-15",List.class);
                        hotelsEntity.getBody().stream().forEach(s->activationContext.appendMessage(s.toString()));

                    }
            );


            return () -> {
                registration.remove();
                flightRegistration.remove();
                hotelRegistration.remove();
                button.setEnabled(false);
            };
        });
        // Layouting

        VerticalLayout chatContainer = new VerticalLayout();
        chatContainer.addClassNames(Flex.AUTO, Overflow.HIDDEN);



        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassNames(Flex.AUTO, Overflow.HIDDEN);

        Aside side = new Aside();
        side.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW_NONE, Flex.SHRINK_NONE, Background.CONTRAST_5);
        side.setWidth("18rem");
        Header header = new Header();
        header.addClassNames(Display.FLEX, FlexDirection.ROW, Width.FULL, AlignItems.CENTER, Padding.MEDIUM,
                BoxSizing.BORDER);
        H3 channels = new H3("Channels");
        channels.addClassNames(Flex.GROW, Margin.NONE);
        CollaborationAvatarGroup avatarGroup = new CollaborationAvatarGroup(userInfo, "chat");
        avatarGroup.setMaxItemsVisible(4);
        avatarGroup.addClassNames(Width.AUTO);

        header.add(channels, avatarGroup);

        side.add(header, tabs);
        buttonLayout.add(singleFileUpload,button,eligibility,flights,hotels);
        chatContainer.add(list,buttonLayout);
        add(chatContainer, side);
        setSizeFull();
        expand(list);

        // Change the topic id of the chat when a new tab is selected
        tabs.addSelectedChangeListener(event -> {
            currentChat = ((ChatTab) event.getSelectedTab()).getChatInfo();
            currentChat.resetUnread();
            list.setTopic(currentChat.getCollaborationTopic());
        });
    }

    private ChatTab createTab(ChatInfo chat) {
        ChatTab tab = new ChatTab(chat);
        tab.addClassNames(JustifyContent.BETWEEN);

        Span badge = new Span();
        chat.setUnreadBadge(badge);
        badge.getElement().getThemeList().add("badge small contrast");
        tab.add(new Span("#" + chat.getName()), badge);

        return tab;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Page page = attachEvent.getUI().getPage();
        page.retrieveExtendedClientDetails(details -> {
            setMobile(details.getWindowInnerWidth() < 740);
        });
        page.addBrowserWindowResizeListener(e -> {
            setMobile(e.getWidth() < 740);
        });
    }

    private void setMobile(boolean mobile) {
        tabs.setOrientation(mobile ? Orientation.HORIZONTAL : Orientation.VERTICAL);
    }

}
