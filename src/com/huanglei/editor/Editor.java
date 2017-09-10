package com.huanglei.editor;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Editor extends JFrame {

    private static final String[] keywords = {"public","static","void","if","else","for","while","break","continue","do","private",
                                                 "protected","null","this","extends","implement","default","boolean","new","false","true",
                                                 "package","import","class","enum","int","char","byte","short","long","switch","case","final",
                                                 "double","float","interface"};
    private JMenuBar menuBar;
    private JMenuItem jmiOpen;
    private JMenuItem jmiClose;
    private JMenuItem jmiSave;
    private JMenuItem jmiSaveAs;
    private JMenuItem jmiQuit;
    private JMenuItem jmiCopy;
    private JMenuItem jmiCut;
    private JMenuItem jmiPaste;
    private JMenuItem jmiDelete;
    private JMenuItem jmiFind;
    private JMenuItem jmiText;
    private JTextPane textPane;
    private JTextField jtfFilePath;
    private boolean fileIsClean;
    private static int index = -1;

    private File currentFile = null;
    private Tools tools = null;

    private Editor(int width,int height){
        this.setBounds(50,50, width, height);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initWidget();
        createUI();
        addListener();
        tools = new Editor.Tools();
        fileIsClean = true;
        this.setVisible(true);
    }

    private void initWidget(){
        jmiText = new JMenuItem("Text");
        jmiFind = new JMenuItem("Find");
        jmiDelete = new JMenuItem("Delete");
        jmiPaste = new JMenuItem("Paste");
        jmiCut = new JMenuItem("Cut");
        jmiCopy = new JMenuItem("Copy");
        jmiQuit = new JMenuItem("Quit");
        jmiSaveAs = new JMenuItem("Save as");
        jmiSave = new JMenuItem("Save");
        jmiClose = new JMenuItem("Close");
        jmiOpen = new JMenuItem("Open");

        JMenu menuSetting = new JMenu("Setting");
        JMenu menuEdit = new JMenu("Edit");
        JMenu menuFile = new JMenu("File");

        menuBar = new JMenuBar();

        textPane = new JTextPane();
        jtfFilePath = new JTextField();
        jtfFilePath.setText("No file");
        jtfFilePath.setEditable(false);

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

    }

    private void createUI(){
        this.setJMenuBar(menuBar);
        this.add(textPane,BorderLayout.CENTER);
        this.add(jtfFilePath,BorderLayout.SOUTH);
    }

    private void addListener(){
        jmiOpen.addActionListener(new MyListener());
        jmiClose.addActionListener(new MyListener());
        jmiSave.addActionListener(new MyListener());
        jmiSaveAs.addActionListener(new MyListener());
        jmiQuit.addActionListener(new MyListener());
        jmiCopy.addActionListener(new MyListener());
        jmiCut.addActionListener(new MyListener());
        jmiPaste.addActionListener(new MyListener());
        jmiDelete.addActionListener(new MyListener());
        jmiFind.addActionListener(new MyListener());
        jmiText.addActionListener(new MyListener());
        textPane.getDocument().addDocumentListener(new MyListener());
    }

    private class MyListener implements ActionListener,DocumentListener{

        private Style keywordStyle;
        private Style classStyle;
        private Style normalStyle;

        private MyListener(){
            keywordStyle = textPane.getStyledDocument().addStyle("keywordStyle",null);
            classStyle = textPane.getStyledDocument().addStyle("classStyle",null);
            normalStyle = textPane.getStyledDocument().addStyle("normalStyle",null);
            StyleConstants.setForeground(keywordStyle,Color.BLUE);
            StyleConstants.setForeground(classStyle,Color.RED);
        }

        public void actionPerformed(ActionEvent e) {
            JMenuItem jmi = (JMenuItem)e.getSource();
            if(jmi == jmiOpen) tools.open();
            else if(jmi == jmiClose) tools.close();
            else if(jmi == jmiSave) tools.saveFile();
            else if(jmi == jmiSaveAs) tools.saveasFile();
            else if(jmi == jmiQuit) tools.quit();
            else if(jmi == jmiCopy) textPane.copy();
            else if(jmi == jmiCut) textPane.cut();
            else if(jmi == jmiPaste) textPane.paste();
            else if(jmi == jmiDelete) textPane.cut();
            else if(jmi == jmiFind) tools.find();
            else if(jmi == jmiText) tools.textSetting();
        }

        public void insertUpdate(DocumentEvent e) {
            if(fileIsClean) fileIsClean = false;
            String fullString = textPane.getText();
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
        private void coloring(String fullString,int start,int len){
            StyleConstants.setForeground(normalStyle,textPane.getForeground());
            String[] keyWord = fullString.substring(start,len).split(" ");
            for(String check:keyWord){
                for(String keyword:keywords){
                    final int _start = fullString.indexOf(check,start);
                    final int _len = check.length();
                    if(check.equals(keyword)){
                        EventQueue.invokeLater(()->textPane.getStyledDocument().setCharacterAttributes(_start,_len,keywordStyle,true));
                        break;
                    }else EventQueue.invokeLater(()->textPane.getStyledDocument().setCharacterAttributes(_start,_len,normalStyle,true));
                }
            }
        }
        public void removeUpdate(DocumentEvent e) {
            if(fileIsClean) fileIsClean = false;
            int offset = e.getOffset();
            String fullString = textPane.getText();
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
    }

    private class Tools implements ActionListener,ItemListener{

        private JFileChooser chooser = new JFileChooser();
        private JDialog findDialog;
        private JDialog textDialog;
        private JTextField jtfFind;
        private JTextField jtfReplace;
        private JButton jbFindNext;
        private JButton jbReplace;
        private JComboBox<String> textSize;
        private JButton jbColorSelect;
        private JComboBox<String> textFont;

        private Tools(){
            findDialog = new JDialog(Editor.this);
            findDialog.setLayout(new GridLayout(4,1));
            findDialog.setBounds(100,100,200,150);
            findDialog.setTitle("Find and Replace");
            textDialog = new JDialog(Editor.this);
            textDialog.setLayout(new GridLayout(3,2));
            textDialog.setBounds(100,100,300,120);
            jtfFind = new JTextField();
            jtfReplace = new JTextField();
            jbFindNext = new JButton("Find Next");
            jbFindNext.addActionListener(this);
            jbReplace = new JButton("Replace");
            jbReplace.addActionListener(this);
            findDialog.add(jtfFind);
            findDialog.add(jtfReplace);
            findDialog.add(jbFindNext);
            findDialog.add(jbReplace);
            textSize = new JComboBox<>();
            textSize.addItemListener(this);
            jbColorSelect = new JButton("Select");
            jbColorSelect.addActionListener(this);
            textFont = new JComboBox<>();
            addItem();
            textFont.addItemListener(this);
            textDialog.add(new JLabel("Size"));
            textDialog.add(textSize);
            textDialog.add(new JLabel("Color"));
            textDialog.add(jbColorSelect);
            textDialog.add(new JLabel("Font"));
            textDialog.add(textFont);
        }

        private void addItem(){
            for(int i = 12;i<24;i+=2) textSize.addItem(String.valueOf(i));
            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for(String fontName:fonts) textFont.addItem(fontName);
        }
        private void open(){
            if(!fileIsClean) {
                int select = JOptionPane.showConfirmDialog(Editor.this,"Save file ?");
                if(select == JOptionPane.YES_OPTION) {
                    if(saveFile() == -1) return ;
                    fileIsClean = true;
                }else if(select == JOptionPane.CANCEL_OPTION) return;
            }
            File file = openFile();
            if(file == null) return;
            try{
                StringBuilder fileContent = new StringBuilder("");
                FileInputStream fis = new FileInputStream((file));
                byte[] buffer = new byte[512];
                while(fis.read(buffer) != -1) fileContent.append(new String(buffer).trim());
                textPane.setText(new String(fileContent));
                currentFile = file;
                jtfFilePath.setText(file.getAbsolutePath());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private int writeFile(File file){
            try{
                FileOutputStream fos = new FileOutputStream((file));
                byte[] content = textPane.getText().getBytes();
                fos.write(content);
                fos.flush();
                fos.close();
                return 1;
            }catch (Exception e){
                e.printStackTrace();
                return -1;
            }
        }
        private int saveFile(){
            if(currentFile == null) return saveasFile();
            return writeFile(currentFile);
        }
        private int  saveasFile(){
            int select = chooser.showSaveDialog(Editor.this);
            if(select == JFileChooser.APPROVE_OPTION) {
                File newFile = new File(chooser.getCurrentDirectory(),chooser.getSelectedFile().getName());
                return writeFile(newFile);
            }
            return -1;
        }
        private File openFile(){
            int select = chooser.showOpenDialog(Editor.this);
            if(select == JFileChooser.CANCEL_OPTION) return null;
            return chooser.getSelectedFile();
        }
        private int close(){
            if(!fileIsClean){
                int select = JOptionPane.showConfirmDialog(Editor.this,"Save file ?");
                if(select == JOptionPane.YES_OPTION) {
                    if(saveFile() == -1) return -1;
                    fileIsClean = true;
                }else if(select == JOptionPane.CANCEL_OPTION) return -1;
            }
            currentFile = null;
            textPane.setText("");
            jtfFilePath.setText("No file");
            return 1;
        }
        private void quit(){
            if(close() == -1) return;
            System.exit(0);
        }
        private void find(){
            findDialog.setVisible(true);
        }
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton)e.getSource();
            if(source == jbFindNext) findNext();
            else if(source == jbReplace) replace();
            else if(source == jbColorSelect){
               Color color = JColorChooser.showDialog(Editor.this,"Select a color",Color.BLACK);
               if(color == null) return;
               textPane.setForeground(color);
            }
        }

        private void findNext(){
            String findString = jtfFind.getText();
            String fullString = textPane.getText();
            if(findString == null || findString.equals("")|| fullString.equals("")) return;
            if(!fullString.substring(index+1).contains(findString)) index = -1;
            index = fullString.indexOf(findString,index + 1);
            if(index == -1) return;
            textPane.setSelectionStart(index);
            textPane.setSelectionEnd(index + findString.length());
            textPane.setSelectionColor(Color.PINK);
        }
        private void replace(){
            String replaceString = jtfReplace.getText();
            if(replaceString == null || replaceString.equals("") || index < 0 ) return;
            textPane.setSelectionStart(index);
            textPane.setSelectionEnd(index + replaceString.length()-1);
            textPane.replaceSelection(replaceString);
        }
        private void textSetting(){ textDialog.setVisible(true);}

        public void itemStateChanged(ItemEvent e) {
            JComboBox source = (JComboBox)e.getSource();
            Font oldFont = textPane.getFont();
            Font newFont = null;
            Object selectSize = textSize.getSelectedItem();
            if(selectSize == null) return;
            if(source == textSize) newFont = new Font(oldFont.getName(),oldFont.getStyle(),Integer.valueOf((String)selectSize));
            else if(source == textFont) newFont = new Font((String)textFont.getSelectedItem(),oldFont.getStyle(),oldFont.getSize());
            textPane.setFont(newFont);
        }
    }
    public static void main(String[] args){
        new Editor(700,600);
    }
}
