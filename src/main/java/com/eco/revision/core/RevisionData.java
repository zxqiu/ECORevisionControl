package com.eco.revision.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.eco.utils.misc.Dict;
import com.eco.utils.misc.Serializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Date;

/**
 * Created by neo on 8/12/18.
 */
public class RevisionData implements Serializable, Serializer<RevisionData> {
    static final long serialVersionUID = 529269941459L;

    @JsonProperty
    private String comment;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public RevisionData() {}

    public RevisionData(String comment) {
        this.comment = comment;
    }

    public RevisionData(InputStream is) throws NullPointerException, IOException, ClassNotFoundException {
        if (is == null) {
            throw new NullPointerException("InputStream is null when build RevisionData");
        }

        ObjectInputStream ois = new ObjectInputStream(is);
        RevisionData revisionData = deserialize(ois);

        is.close();

        this.comment = revisionData.comment;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(RevisionData revisionData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(revisionData);
        oos.close();
    }

    @Override
    public RevisionData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        RevisionData revisionData = (RevisionData) ois.readObject();
        ois.close();
        return revisionData;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static void main(String[] arg) throws IOException, ClassNotFoundException {
        RevisionData testRevisionData = new RevisionData("testComment");

        byte[] bytes = testRevisionData.toByteArray();
        RevisionData revisionData = new RevisionData(new ByteArrayInputStream(bytes));

        System.out.println(revisionData.toString());
    }
}
