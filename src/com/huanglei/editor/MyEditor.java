package com.huanglei.editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MyEditor extends JFrame {

    private boolean fileIsClean = true;
    private File buffer = null;
    public JDialog findDialog;
    public JDialog textDialog;
    public MyEditor(){
        this.setBounds(150,100, 600, 500);
        this.setLayout(new BorderLayout());
        UICreater uiCreater = new UICreater(this);
        uiCreater.createMenuBar();
        uiCreater.createPane();
        uiCreater.createStatueBar();
        textDialog = uiCreater.createTextDialog();
        findDialog  = uiCreater.createFindDialog();
        new EventHandler(this);
        this.setVisible(true);
    }

    public boolean getStatus(){
        return fileIsClean;
    }
    public void setFileStatus(boolean b){
        fileIsClean = b;
    }
    public boolean hasBuffer(){
        if(buffer == null) return false;
        return true;
    }

    public String getText(){
        JTextPane textPane = (JTextPane)getContentPane().getComponent(0);
        return textPane.getText();
    }
    public void setText(String str){
        JTextPane textPane = (JTextPane)getContentPane().getComponent(0);
        textPane.setText(str);
    }
    public File getBuffer(){
        return buffer;
    }

    public void setBuffer(File file ){
        buffer = file;
    }

    public void setFilePath(String path){
        JTextField textField = (JTextField)getContentPane().getComponent(1);
        textField.setText(path);
    }

    public static  void main(String[] args){
        new MyEditor();
    }
}
