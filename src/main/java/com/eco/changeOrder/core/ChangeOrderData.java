package com.eco.changeOrder.core;

import com.eco.utils.misc.Serializer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.*;
import java.util.List;

/**
 * Created by neo on 9/1/18.
 */
public class ChangeOrderData implements Serializable, Serializer<ChangeOrderData> {
    static final long serialVersionUID = -12324123124123L;
    @JsonProperty
    private String comment;

    @JsonProperty
    private List<Bug> bugs;

    public  ChangeOrderData() {}

    public ChangeOrderData(String comment, List<Bug> bugs) {
        this.comment = comment;
        this.bugs = bugs;
    }

    public ChangeOrderData(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(is);
        ChangeOrderData changeOrderData = deserialize(ois);

        this.comment = changeOrderData.getComment();
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(ChangeOrderData changeOrderData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(changeOrderData);
        oos.close();
    }

    @Override
    public ChangeOrderData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ChangeOrderData revisionData = (ChangeOrderData) ois.readObject();
        ois.close();
        return revisionData;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Bug> getBugs() {
        return bugs;
    }

    public void setBugs(List<Bug> bugs) {
        this.bugs = bugs;
    }
}
