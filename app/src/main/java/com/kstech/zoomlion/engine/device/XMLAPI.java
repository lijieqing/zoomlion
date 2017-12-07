package com.kstech.zoomlion.engine.device;


import com.kstech.zoomlion.model.xml.XMLAttribute;
import com.kstech.zoomlion.model.xml.XMLBase;
import com.kstech.zoomlion.model.xml.XMLHasKids;
import com.kstech.zoomlion.model.xml.XmlGenerate;
import com.kstech.zoomlion.model.xml.XmlReader;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/6/7.
 */
public class XMLAPI {
    private static Document document = null;

    //返回 xmlbean 包下的根目录

    /**
     * Read xml object.
     *
     * @param inputStream the input stream of xml file
     * @return the object
     */
    public static Object readXML(InputStream inputStream) {
        Element rootElement = null;
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(inputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //先 将root 节点解析出来
        if (document != null) rootElement = document.getRootElement();
        XMLBase root = new XMLHasKids(rootElement.getName());
        List<Attribute> attributes = rootElement.attributes();
        List<XMLAttribute> xmlAttributes = new ArrayList<>();
        for (Attribute attribute : attributes) {
            xmlAttributes.add(new XMLAttribute(attribute.getName(), attribute.getValue()));
        }
        root.setXMLAttributes(xmlAttributes);

        //利用递归 将子节点逐一解析 放入xmlbase 中
        List<Element> childelement = rootElement.elements();
        for (Element element : childelement) {
            XmlReader.XMLparse(element, root);
        }

        return root.transform();
    }

    /**
     * Read xml object.
     *
     * @param xmlText the input stream of xml text
     * @return the object
     */
    public static Object readXML(String xmlText) {
        Element rootElement = null;
        try {
            document = DocumentHelper.parseText(xmlText);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //先 将root 节点解析出来
        if (document != null) rootElement = document.getRootElement();
        XMLBase root = new XMLHasKids(rootElement.getName());
        List<Attribute> attributes = rootElement.attributes();
        List<XMLAttribute> xmlAttributes = new ArrayList<>();
        for (Attribute attribute : attributes) {
            xmlAttributes.add(new XMLAttribute(attribute.getName(), attribute.getValue()));
        }
        root.setXMLAttributes(xmlAttributes);

        //利用递归 将子节点逐一解析 放入xmlbase 中
        List<Element> childelement = rootElement.elements();
        for (Element element : childelement) {
            XmlReader.XMLparse(element, root);
        }

        return root.transform();
    }


    /**
     * writeXML2File.
     *
     * @param object  数据源，待转化成xml的实体类
     * @param DesPath xml文件生成路径
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws IOException               the io exception
     */
    public static void writeXML2File(Object object, String path) throws IllegalAccessException, IOException, InvocationTargetException {
        XmlGenerate.generate(object, path);
    }

}
