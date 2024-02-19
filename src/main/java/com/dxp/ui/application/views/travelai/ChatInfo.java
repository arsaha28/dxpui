package com.dxp.ui.application.views.travelai;

import com.vaadin.flow.component.html.Span;
import lombok.Data;

@Data
public class  ChatInfo {
    private String name;
    private int unread;
    private Span unreadBadge;

    public ChatInfo(String name, int unread) {
        this.name = name;
        this.unread = unread;
    }

    public void resetUnread() {
        unread = 0;
        updateBadge();
    }

    public void incrementUnread() {
        unread++;
        updateBadge();
    }

    private void updateBadge() {
        unreadBadge.setText(unread + "");
        unreadBadge.setVisible(unread != 0);
    }

    public void setUnreadBadge(Span unreadBadge) {
        this.unreadBadge = unreadBadge;
        updateBadge();
    }

    public String getCollaborationTopic() {
        return "chat/" + name;
    }
}