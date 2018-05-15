package com.ovvi.remotelocation.model;

import android.widget.ImageView;

/**
 * 菜单信息
 * 
 * @author chensong
 * 
 */
public class ItemMenu {
    /** 菜单的图片 */
    private int menuView;
    /** 菜单名 */
    private String menuTitle;
    /** 所选择的内容 */
    private String menuContent;

    public ItemMenu(int imageView, String menuTitle, String menuContent) {
        // TODO Auto-generated constructor stub
        this.menuView = imageView;
        this.menuTitle = menuTitle;
        this.menuContent = menuContent;
    }

    public int getMenuView() {
        return menuView;
    }

    public void setMenuView(int menuView) {
        this.menuView = menuView;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTile) {
        this.menuTitle = menuTile;
    }

    public String getMenuContent() {
        return menuContent;
    }

    public void setMenuContent(String menuContent) {
        this.menuContent = menuContent;
    }

}
