package com.kstech.zoomlion.model.xml;

import com.kstech.zoomlion.model.xmlbean.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/7.
 */
public class XmlReader {
    public static void main(String[] args) {

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     * xml文件 读取到 xmlbase 中 准备transform 到 com.lee.xmlbean
     */
        InputStream is = null;
        Document document = null;
        Element rootElement = null;
        SAXReader reader = new SAXReader();
        try {
            is = new FileInputStream(new File("D:/temp.xml"));
            document = reader.read(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //先 将root 节点解析出来
        if (document != null)rootElement = document.getRootElement();
        XMLBase root = new XMLHasKids(rootElement.getName());
        List<Attribute> attributes = rootElement.attributes();
        List<XMLAttribute> xmlAttributes = new ArrayList<>();
        for (Attribute attribute : attributes) {
            xmlAttributes.add(new XMLAttribute(attribute.getName(),attribute.getValue()));
        }
        root.setXMLAttributes(xmlAttributes);

        //利用递归 将子节点逐一解析 放入xmlbase 中
        List<Element> childelement = rootElement.elements();
        for (Element element : childelement) {
            XMLparse(element,root);
        }

        //xml文件解析完成后，利用root节点 进行transform
        Object o = root.transform();

        System.out.println(o instanceof Device);
    /**
     * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
     * xml文件 读取到 xmlbase 中 准备transform 到 com.lee.xmlbean
     */


    }
    public static void XMLparse(Element rootElement,XMLBase root){
        XMLBase cur;
        if (rootElement.elements().size()>0){
            cur = new XMLHasKids(rootElement.getName());
            List<Attribute> attributes = rootElement.attributes();
            List<XMLAttribute> xmlAttributes = new ArrayList<>();
            for (Attribute attribute : attributes) {
                xmlAttributes.add(new XMLAttribute(attribute.getName(),attribute.getValue()));
            }
            cur.setXMLAttributes(xmlAttributes);
            root.addKids(cur);
            List<Element> childelement = rootElement.elements();
            for (Element element : childelement) {
                XMLparse(element,cur);
            }
        }else {
            cur = new XMLNoChilds(rootElement.getName());
            List<Attribute> attributes = rootElement.attributes();
            List<XMLAttribute> xmlAttributes = new ArrayList<>();
            for (Attribute attribute : attributes) {
                xmlAttributes.add(new XMLAttribute(attribute.getName(),attribute.getValue()));
            }
            cur.setXMLAttributes(xmlAttributes);
            root.addKids(cur);
        }
    }
}
