package com.dxp.ui.application.views.insight;

import com.dxp.ui.application.config.DXPConfiguration;
import com.dxp.ui.application.load.ImportData;
import com.dxp.ui.application.model.TransactionClassificationRequest;
import com.dxp.ui.application.model.TransactionML;
import com.dxp.ui.application.views.MainLayout;
import com.googlecode.gentyref.TypeToken;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@PageTitle("InsightAI")
@Route(value = "insight", layout = MainLayout.class)
@RouteAlias(value = "insight", layout = MainLayout.class)
public class InsightAIView extends HorizontalLayout {

    @Autowired
    private RestTemplate restTemplate;

    List<TransactionML> trxList = null;

    @Autowired
    private DXPConfiguration dxpConfiguration;

    public InsightAIView() {

    }

    @PostConstruct
    public void init() {
        addClassNames("insight-ai-view", LumoUtility.Width.FULL, LumoUtility.Display.FLEX, LumoUtility.Flex.AUTO);
        setSpacing(false);

        Grid<TransactionML> grid = new Grid<>(TransactionML.class, false);
        grid.addColumn(TransactionML::getDate).setHeader("Date");
        grid.addColumn(TransactionML::getAmount).setHeader("Amount");
        grid.addColumn(TransactionML::getMerchantName).setHeader("Merchant Name");
        grid.addColumn(TransactionML::getLocation).setHeader("Location");
        grid.addColumn(TransactionML::getMccCode).setHeader("MCC");
        grid.addColumn(TransactionML::getCategory).setHeader("Category");
        grid.addColumn(TransactionML::getIsRecurring).setHeader("Recurring");

        Button categorize = getButton(grid,dxpConfiguration.getCategorizationEndpoint(),"Categorize");
        Button cpa = getButton(grid,dxpConfiguration.getCpaEndpoint(),"CPA");

        Button importData = new Button("Import");
        importData.setEnabled(true);


        importData.addClickListener(clickEvent -> {
            try {
                trxList = ImportData.loadData(5);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            grid.setItems(trxList);
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassNames(LumoUtility.Flex.AUTO, LumoUtility.Overflow.HIDDEN);
        buttonLayout.add(importData);
        buttonLayout.add(categorize);
        buttonLayout.add(cpa);
        buttonLayout.add(checkNewTransaction());


        VerticalLayout txtLayout = new VerticalLayout();
        txtLayout.addClassNames(LumoUtility.Flex.AUTO, LumoUtility.Overflow.HIDDEN);
        txtLayout.add(grid);

        VerticalLayout container = new VerticalLayout();
        container.add(txtLayout,buttonLayout);
        add(container);
    }

    private Button checkNewTransaction(){
        Dialog dialog = new Dialog();
        dialog.setWidth("1000");
        dialog.setWidth("1000");
        dialog.setHeaderTitle("Transaction Classification");

        TextField merchant = new TextField("Merchant");
        TextField categoryCode = new TextField("MCC");
        TextField amount = new TextField("Amount");
        TextField location = new TextField("location");
        TextField category = new TextField("category");

        FormLayout formLayout = new FormLayout(merchant,categoryCode,amount,location,category);
        dialog.add(formLayout);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(merchant, 1);

        Button saveButton = new Button("Check", e -> {
            TransactionClassificationRequest  request = new TransactionClassificationRequest(amount.getValue(),merchant.getValue(),location.getValue(),categoryCode.getValue());
            new Gson().toJson(request);
            ResponseEntity<String> responseEntity = restTemplate
                    .postForEntity(dxpConfiguration.getCategorizationEndpointV2(),new Gson().toJson(Arrays.asList(request)),String.class);
            category.setValue(responseEntity.getBody());
            System.out.println(responseEntity.getBody());
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        Button eligibility = new Button("Eligibility", e -> dialog.open());
        eligibility.setEnabled(true);
        return eligibility;
    }

    private Button getButton(Grid<TransactionML> grid,String endpoint,String label) {
        Button button = new Button(label);
        button.setEnabled(true);


        button.addClickListener(clickEvent -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String input = new Gson().toJson(trxList);
            System.out.println(input);
            HttpEntity<String> entity = new HttpEntity<String>(input, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint,entity, String.class);
            trxList = new Gson().fromJson(response.getBody().toString(),new TypeToken<List<TransactionML>>(){}.getType());
            grid.setItems(trxList);
        });
        return button;
    }
}
