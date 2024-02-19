package com.dxp.ui.application.views.mindmap;

import com.dxp.ui.application.views.MainLayout;
import com.dxp.ui.application.views.travelai.ChatInfo;
import com.dxp.ui.application.views.travelai.ChatTab;
import com.vaadin.collaborationengine.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@PageTitle("SearchAI")
@Route(value = "search", layout = MainLayout.class)
@RouteAlias(value = "search", layout = MainLayout.class)
public class PersonalisedSearch extends HorizontalLayout {

    @Autowired
    private RestTemplate restTemplate;

    private ChatInfo[] chats = new ChatInfo[]{new ChatInfo("general", 0), new ChatInfo("support", 0),
            new ChatInfo("casual", 0)};
    private ChatInfo currentChat = chats[0];
    private Tabs tabs;

    public PersonalisedSearch() {
        addClassNames("search-view", Width.FULL, Display.FLEX, Flex.AUTO);
        setSpacing(false);
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

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
        TextField field = new TextField("Message");

        Button button = new Button("Submit");
        button.setEnabled(false);

        list.setSubmitter(activationContext -> {
            button.setEnabled(true);
            Registration registration = button.addClickListener(
                    event -> {
                        activationContext.appendMessage(field.getValue());
                    });
            return () -> {
                registration.remove();
                button.setEnabled(false);
            };
        });

        // Layouting

        VerticalLayout chatContainer = new VerticalLayout();
        chatContainer.addClassNames(Flex.AUTO, Overflow.HIDDEN);

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

        chatContainer.add(list, field,button);
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
