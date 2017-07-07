package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/7/6.
 */

/**
 * assets 文件夹中resource.xml对应标签
 */
public class Msg {
    private String Content;
    private String RefName;
    private String Id;

    public Msg() {
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getRefName() {
        return RefName;
    }

    public void setRefName(String refName) {
        RefName = refName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "Content='" + Content + '\'' +
                ", RefName='" + RefName + '\'' +
                ", Id='" + Id + '\'' +
                '}';
    }
}
