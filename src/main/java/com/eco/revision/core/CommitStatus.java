package com.eco.revision.core;

import com.eco.utils.misc.Dict;
import com.eco.utils.misc.Serializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.FormParam;
import java.io.*;

/**
 * Created by neo on 8/27/18.
 */
public class CommitStatus implements Serializable, Serializer<CommitStatus> {
    @JsonProperty
    private String branchName;

    @JsonProperty
    private int status;

    @JsonProperty
    private String comment;

    public CommitStatus() {
    }

    public CommitStatus(String branchName, int status, String comment) {
        this.branchName = branchName;
        this.status = status;
        this.comment = comment;
    }

    public CommitStatus(InputStream is) throws IOException, ClassNotFoundException {
        if (is == null) {
            throw new NullPointerException("InputStream is null when build RevisionData");
        }

        ObjectInputStream ois = new ObjectInputStream(is);
        CommitStatus commitStatus = deserialize(ois);

        is.close();

        this.comment = commitStatus.comment;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    @Override
    public void serialize(CommitStatus commitStatus, ObjectOutputStream oos) throws IOException {
        oos.writeObject(commitStatus);
        oos.close();
    }

    @Override
    public CommitStatus deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        CommitStatus commitStatus = (CommitStatus) ois.readObject();
        ois.close();
        return commitStatus;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
