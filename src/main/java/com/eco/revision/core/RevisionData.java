package com.eco.revision.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.eco.utils.misc.Serializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 8/12/18.
 */
public class RevisionData implements Serializable, Serializer<RevisionData> {
    static final long serialVersionUID = 529269941459L;

    @JsonProperty
    private List<CommitStatus> commitStatuses;

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

    public RevisionData(List<CommitStatus> commitStatuses) {
    }

    public RevisionData(InputStream is) throws NullPointerException, IOException, ClassNotFoundException {
        if (is == null) {
            throw new NullPointerException("InputStream is null when build RevisionData");
        }

        ObjectInputStream ois = new ObjectInputStream(is);
        RevisionData revisionData = deserialize(ois);

        is.close();

        this.commitStatuses = revisionData.commitStatuses;
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

    public List<CommitStatus> getCommitStatuses() {
        return commitStatuses;
    }

    public void setCommitStatuses(List<CommitStatus> commitStatuses) {
        this.commitStatuses = commitStatuses;
    }

    public static void main(String[] arg) throws IOException, ClassNotFoundException {
        List<CommitStatus> commitStatuses = new ArrayList<>();
        commitStatuses.add(new CommitStatus("testBranch1", 0, "testComment1"));
        RevisionData testRevisionData = new RevisionData(commitStatuses);

        byte[] bytes = testRevisionData.toByteArray();
        RevisionData revisionData = new RevisionData(new ByteArrayInputStream(bytes));

        System.out.println(revisionData.toString());
    }
}
