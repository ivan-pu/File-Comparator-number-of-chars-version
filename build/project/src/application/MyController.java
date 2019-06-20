package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class MyController implements Initializable{

    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private TextField textfield;
    @FXML
    private Button submit;
    @FXML
    private Text waiting;
    private String file1;
    private String file2;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }
    
    public void button1click(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Word files (*.doc or *.docx)", "*.doc","*.docx"));
            File file = fileChooser.showOpenDialog(null);
            String path = file.getAbsolutePath();
            WordExtractor docextractor;
            XWPFWordExtractor docxextractor;
            FileInputStream fis = new FileInputStream(path);
            if (path.endsWith("doc")) {
                docextractor = new WordExtractor(fis);
                file1 = docextractor.getText();
            }
            if (path.endsWith("docx")) {
              XWPFDocument xdoc = new XWPFDocument(fis);
              docxextractor = new XWPFWordExtractor(xdoc);
              file1 = docxextractor.getText();
            }
            file1 = filterText(file1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void button2click(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Word files (*.doc or *.docx)", "*.doc","*.docx"));
            File file = fileChooser.showOpenDialog(null);
            String path = file.getAbsolutePath();
            WordExtractor docextractor;
            XWPFWordExtractor docxextractor;
            FileInputStream fis = new FileInputStream(path);
            if (path.endsWith("doc")) {
                docextractor = new WordExtractor(fis);
                file2 = docextractor.getText();
            }
            if (path.endsWith("docx")) {
              XWPFDocument xdoc = new XWPFDocument(fis);
              docxextractor = new XWPFWordExtractor(xdoc);
              file2 = docxextractor.getText();
            }
            file2 = filterText(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String filterText(String str) {//保留文字+中文标点
        String regex = "[a-zA-Z0-9\u3002\uff1f\uff01\uff0c\u3001\uff1b\uff1a\u201c"
                        + "\u201d\u2018\u2019\uff08\uff09\u300a\u300b\u3008\u3009\u3010\u3011\u300e"
                        + "\u300f\u300c\u300d\ufe43\ufe44\u3014\u3015\u2026\u2014\uff5e\ufe4f\uffe5"
                        + "\r\n\\u4E00-\\u9FA5]*";
        String result = "";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            result+= matcher.group(0);
        }
        return result;
    }
    
    public List<String> removeDups(List<String> list){
        List<String> result = new ArrayList<String>();
        for (String temp: list) {
            if (result.indexOf(temp) == -1) {
                result.add(temp);
            }
        }
        return result;
    }
    
    public void start(ActionEvent event) {
        int count = 0;//重复度count
        int sensitivity = Integer.parseInt(textfield.getText());
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        for (int i = 0; i <= file1.length() - sensitivity; i++) {
            list1.add(file1.substring(i, i + sensitivity));
        }
        for (int i = 0; i <= file2.length() - sensitivity; i++) {
            list2.add(file2.substring(i, i + sensitivity));
        }
        list1 = removeDups(list1);
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String s1: list1) {
            for (String s2: list2) {
                if (s1.equals(s2)) {
                    if (map.get(s1) == null) {
                        map.put(s1,1);
                        count++;
                    }
                    else {
                        int temp = map.get(s1);
                        map.put(s1,++temp);
                    }
                }
            }
        }
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("Txt files (*.txt)", "*.txt"));
        File output = fileChooser1.showSaveDialog(null);
        BufferedWriter out = null;   
        try {   
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false)));   
            for (String key : map.keySet()) {
                out.write(key);
                out.newLine();
                out.write(map.get(key) + "次\r\n");
                out.write("----------------------------------------------------\r\n");
            }
            out.write("重复度(大约)" + (double)count/list1.size());  
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
            if(out != null){
            out.close();   
                }
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        } 
        waiting.setText("已输出为" + output);


        
    }

}
