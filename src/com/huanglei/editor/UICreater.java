package com.huanglei.editor;

import javax.swing.*;
import java.awt.*;

public class UICreater {

    private MyEditor editor;

    public UICreater(MyEditor editor){
        this.editor = editor;
    }

    public void createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuEdit = new JMenu("Edit");
        JMenu menuSetting = new JMenu("Setting");

        JMenuItem jmiText = new JMenuItem("Text");
        JMenuItem jmiFind = new JMenuItem("Find");
        JMenuItem jmiDelete = new JMenuItem("Delete");
        JMenuItem jmiPaste = new JMenuItem("Paste");
        JMenuItem jmiCut = new JMenuItem("Cut");
        JMenuItem jmiCopy = new JMenuItem("Copy");
        JMenuItem jmiQuit = new JMenuItem("Quit");
        JMenuItem jmiSaveAs = new JMenuItem("Save as");
        JMenuItem jmiSave = new JMenuItem("Save");
        JMenuItem jmiClose = new JMenuItem("Close");
        JMenuItem jmiOpen = new JMenuItem("Open");

        menuFile.add(jmiOpen);
        menuFile.add(jmiClose);
        menuFile.add(jmiSave);
        menuFile.add(jmiSaveAs);
        menuFile.add(jmiQuit);

        menuEdit.add(jmiCopy);
        menuEdit.add(jmiCut);
        menuEdit.add(jmiPaste);
        menuEdit.add(jmiDelete);
        menuEdit.add(jmiFind);

        menuSetting.add(jmiText);

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuSetting);

        editor.setJMenuBar(menuBar);

    }
    public void createPane(){
        editor.getContentPane().add(new JTextPane(),BorderLayout.CENTER);
    }

    public void createStatueBar(){
        JTextField tf = new JTextField("No file");
        tf.setEditable(false);
        editor.add(tf,BorderLayout.SOUTH);
    }
    public JDialog createFindDialog(){
        JDialog findDialog = new JDialog(editor);
        findDialog.setBounds(200,200,250,150);
        findDialog.setLayout(new GridLayout(4,1));
        findDialog.setTitle("Find and Replace");
        findDialog.add(new JTextField());
        findDialog.add(new JTextField());
        findDialog.add(new JButton("Next"));
        findDialog.add(new JButton("Replace"));
        return findDialog;
    }
    public JDialog createTextDialog(){
        JDialog textDialog = new JDialog(editor);
        textDialog.setBounds(200,200,250,100);
        textDialog.setModal(true);
        textDialog.setTitle("Text setting");
        textDialog.setLayout(new GridLayout(3,2));
        textDialog.add(new JLabel("Size"));
        JComboBox textSize = new JComboBox<>();
        for(int i = 12;i<24;i+=2) textSize.addItem(String.valueOf(i));
        textDialog.add(textSize );
        textDialog.add(new JLabel("Color"));
        textDialog.add(new JButton("Select"));
        textDialog.add(new JLabel("Font"));
        JComboBox textFont = new JComboBox<>();
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(String fontName:fonts) textFont.addItem(fontName);
        textDialog.add(textFont);
        return textDialog;
    }
}
