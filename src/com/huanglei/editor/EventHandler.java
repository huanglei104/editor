package com.huanglei.editor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class EventHandler extends WindowAdapter implements ActionListener,DocumentListener,ItemListener{

    private static final String[] keywords = {"public","static","void","if","else","for","while","break","continue","do","private",
            "protected","null","this","extends","implement","default","boolean","new","false","true",
            "package","import","class","enum","int","char","byte","short","long","switch","case","final",
            "double","float","interface"};
    private static final String[] pkgs = {"java.lang.","java.io.","java.applet.","java.awt.","java.awt.color.","java.awt.datatransfer.",
            "java.awt.dnd.","java.awt.event.","java.awt.font.","java.awt.print.","java.beans.","java.lang.annotation.","java.lang.ref.",
            "java.math.","java.net.","java.nio","java.sql,","java.text,","java.time.","java.util.","javax.swing.","javax.swing.text."};
    private static int index = -1;

    private MyEditor editor;
    public EventHandler(MyEditor editor){
        this.editor = editor;
        editor.addWindowListener(this);
        int menuCount = editor.getJMenuBar().getMenuCount();
        for(int i=0;i<menuCount;i++){
            int itemCount = editor.getJMenuBar().getMenu(i).getItemCount();
            for(int j=0;j<itemCount;j++){
                editor.getJMenuBar().getMenu(i).getItem(j).addActionListener(this);
            }
        }
        JTextPane textPane = (JTextPane)editor.getContentPane().getComponent(0);
        textPane.getStyledDocument().addDocumentListener(this);
        for(int i = 2;i < 4;i++){
            JButton btn = (JButton) editor.findDialog.getContentPane().getComponent(i);
            btn.addActionListener(this);
        }
        JComboBox jcb = (JComboBox) editor.textDialog.getContentPane().getComponent(1);
        jcb.addItemListener(this);
        JComboBox jcb2 = (JComboBox) editor.textDialog.getContentPane().getComponent(5);
        jcb2.addItemListener(this);
        JButton jbtn = (JButton) editor.textDialog.getContentPane().getComponent(3);
        jbtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
        if(comm.equals("Open")) open();
        if(comm.equals("Close")) close();
        if(comm.equals("Save")) save();
        if(comm.equals("Save as")) saveAs();
        if(comm.equals("Quit")) quit();
        if(comm.equals("Copy")) textHandle("copy");
        if(comm.equals("Paste")) textHandle("paste");
        if(comm.equals("Cut")) textHandle("cut");
        if(comm.equals("Delete")) textHandle("delete");
        if(comm.equals("Find")) {
            int x = editor.findDialog.getParent().getX();
            int y = editor.findDialog.getParent().getY();
            editor.findDialog.setBounds(x+200,y+200,250,150);
            editor.findDialog.setVisible(true);
            editor.repaint();
        }
        if(comm.equals("Next")) findNext();
        if(comm.equals("Replace")) replace();
        if(comm.equals("Text")) {
            int x = editor.textDialog.getParent().getX();
            int y = editor.textDialog.getParent().getY();
            editor.textDialog.setBounds(x+200,y+200,250,100);
            editor.textDialog.setVisible(true);
            editor.repaint();
        }
        if(comm.equals("Select")) selectColor();
    }

    public void insertUpdate(DocumentEvent e) {
        if(editor.getStatus()) editor.setFileStatus(false);
        String fullString = editor.getText();
        int fullLen = fullString.length();
        int offset = e.getOffset();
        int start;
        int end;
        if(offset == 0) {
            if(fullString.charAt(0) == ' ') return;
            start = 0;
        }else {
            if(fullString.charAt(offset) == ' ' && ((offset+1) == fullLen || fullString.charAt(offset+1) == ' ')) return;
            if(fullString.charAt(offset) == ' ' && fullString.charAt(offset-1) == ' ') return;
            int i = offset;
            if(fullString.charAt(offset) == ' ') i--;
            while(fullString.charAt(i) != ' ' && i !=0) i--;
            if(i == 0) start = i;
            else start = i+1;
        }
        int i = offset;
        if(fullString.charAt(offset) == ' ') i++;
        while(fullString.charAt(i) != ' ' && i !=fullLen-1) i++;
        if(i == fullLen-1) end = i;
        else end = i - 1;
        coloring(fullString,start,end + 1);
    }

    public void removeUpdate(DocumentEvent e) {
        if(editor.getStatus()) editor.setFileStatus(false);
        int offset = e.getOffset();
        String fullString = editor.getText();
        int fullLen = fullString.length();
        if(offset > fullLen-1) offset = fullLen-1;
        int start;
        int end;
        if((fullString.trim().equals(""))) return;
        String str = fullString.substring(0,offset);
        if((str.trim().equals("")) && offset != 0) return;
        else {
            if(fullString.charAt(offset) == ' ') {
                int i = offset - 1;
                while(fullString.charAt(i) == ' ') i--;
                end = i;
                while(fullString.charAt(i) != ' ' && i != 0) i--;
                if(i == 0) start = 0;
                else start = i + 1;
            }else {
                int i = offset;
                while(fullString.charAt(i) != ' ' && i != 0) i--;
                if(i == 0) start = 0;
                else start = i + 1;
                i = offset;
                while(fullString.charAt(i) != ' ' && i != fullLen-1) i++;
                if(i == fullLen - 1) end = i;
                else end = i;
            }
        }
        coloring(fullString,start,end + 1);
    }

    public void changedUpdate(DocumentEvent e) {}
    public void windowClosing(WindowEvent e) { quit(); }

    public void itemStateChanged(ItemEvent e) {
        JTextPane textPane = (JTextPane) editor.getContentPane().getComponent(0);
        Font oldFont = textPane.getFont();
        Font newFont;

        JComboBox jcb = (JComboBox) editor.textDialog.getContentPane().getComponent(1);
        Object size = jcb.getSelectedItem();
        JComboBox jcb2 = (JComboBox) editor.textDialog.getContentPane().getComponent(5);
        Object font = jcb2.getSelectedItem();
        if(size == null && font == null) return;
        newFont = new Font((String)font,oldFont.getStyle(),Integer.valueOf((String)size));
        textPane.setFont(newFont);
    }

    private void textHandle(String str){
        JTextPane textPane = (JTextPane)editor.getContentPane().getComponent(0);
        if(str.equals("copy")) textPane.copy();
        if(str.equals("cut") || str.equals("delete")) textPane.cut();
        if(str.equals("paste")) textPane.paste();
    }
    private void open(){
        boolean isClean = editor.getStatus();
        if(!isClean) {
            int select = JOptionPane.showConfirmDialog(editor,"Save file ?");
            if(select == JOptionPane.YES_OPTION) {
                if(!save()) return;
                editor.setFileStatus(true);
            }else if(select == JOptionPane.CANCEL_OPTION) return;
        }
        File file = openFile();
        if(file == null) return;
        try{
            StringBuilder fileContent = new StringBuilder("");
            FileInputStream fis = new FileInputStream((file));
            byte[] buffer = new byte[512];
            while(fis.read(buffer) != -1) fileContent.append(new String(buffer).trim());
            editor.setText(new String(fileContent));
            if(!editor.hasBuffer()) editor.setBuffer(file);
            editor.setFilePath(file.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private File openFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open file");
        int select = chooser.showOpenDialog(editor);
        if(select == JFileChooser.CANCEL_OPTION) return null;
        return chooser.getSelectedFile();
    }
    private boolean close(){
        boolean isClean = editor.getStatus();
        if(!isClean){
            int select = JOptionPane.showConfirmDialog(editor,"Save file ?");
            if(select == JOptionPane.YES_OPTION) {
                if(editor.hasBuffer()) {
                    if(!saveFile(editor.getBuffer())) return false;
                } else {
                    if(!saveAs()) return false;
                }
                editor.setFileStatus(true);
            }else if(select == JOptionPane.CANCEL_OPTION) return false;
        }
        editor.setBuffer(null);
        editor.setText("");
        editor.setFilePath("No file");
        return true;
    }
    private boolean saveFile(File file){
        try{
            FileOutputStream fos = new FileOutputStream((file));
            byte[] content = editor.getText().getBytes();
            fos.write(content);
            fos.flush();
            fos.close();
            editor.setFilePath("No file");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private boolean save(){
        boolean isClean = editor.getStatus();
        if(isClean) return true;
        if(!editor.hasBuffer()) return saveAs();
        return saveFile(editor.getBuffer());
    }
    private boolean saveAs(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save file");
        int select = chooser.showSaveDialog(editor);
        if(select == JFileChooser.APPROVE_OPTION) {
            File newFile = new File(chooser.getCurrentDirectory(),chooser.getSelectedFile().getName());
            return saveFile(newFile);
        }
        return true;
    }

    private void quit(){
        if(!close()) return;
        System.exit(0);
    }

    private void coloring(String string,int start,int len){
        JTextPane textPane = (JTextPane)editor.getContentPane().getComponent(0);
        Style style = textPane.getStyledDocument().addStyle("myStyle",null);
        Style normalStyle = textPane.getStyledDocument().addStyle("normalStyle",null);
        Style classStyle = textPane.getStyledDocument().addStyle("classStyle",null);
        StyleConstants.setForeground(style,Color.BLUE);
        StyleConstants.setForeground(classStyle,Color.RED);
        StyleConstants.setForeground(normalStyle,textPane.getForeground());
        String[] keyWord = string.substring(start,len).split(" ");
        for(String check:keyWord){
            for(String keyword:keywords){
                final int _start = string.indexOf(check,start);
                final int _len = check.length();
                if(check.equals(keyword)){
                    EventQueue.invokeLater(()->textPane.getStyledDocument().setCharacterAttributes(_start,_len,style,true));
                    break;
                }else if(isClass(check)){
                    EventQueue.invokeLater(()->textPane.getStyledDocument().setCharacterAttributes(_start,_len,classStyle,true));
                }else EventQueue.invokeLater(()->textPane.getStyledDocument().setCharacterAttributes(_start,_len,normalStyle,true));
            }
        }
    }
    private void findNext(){
        JTextField tf = (JTextField)editor.findDialog.getContentPane().getComponent(0);
        JTextPane textPane = (JTextPane) editor.getContentPane().getComponent(0);
        String findString = tf.getText();
        String fullString = editor.getText();
        if(findString == null || findString.equals("")|| fullString.equals("")) return;
        if(!fullString.substring(index+1).contains(findString)) index = -1;
        index = fullString.indexOf(findString,index + 1);
        if(index == -1) return;
        textPane.setSelectionStart(index);
        textPane.setSelectionEnd(index + findString.length());
        textPane.setSelectionColor(Color.PINK);
    }
    private void replace(){
        if(index < 0) return;
        JTextField tf = (JTextField)editor.findDialog.getContentPane().getComponent(1);
        String replaceString = tf.getText();
        JTextPane textPane = (JTextPane) editor.getContentPane().getComponent(0);
        textPane.replaceSelection(replaceString);
        findNext();
    }
    private boolean isClass(String clazz){
        for(String pkg:pkgs){
            try {
                Class.forName(pkg+clazz);
                return true;
            } catch (ClassNotFoundException e){}
        }
        return false;
    }
    private void selectColor(){
        Color color = JColorChooser.showDialog(editor,"Select a color",Color.BLACK);
        if(color == null) return;
        JTextPane textPane = (JTextPane)editor.getContentPane().getComponent(0);
        textPane.setForeground(color);
        coloring(textPane.getText(),0,textPane.getText().length());
    }
}
