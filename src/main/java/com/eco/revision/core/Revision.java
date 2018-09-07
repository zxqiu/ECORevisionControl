package com.eco.revision.core;

import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by neo on 8/12/18.
 */

@Entity
@Table(name = Revision.TABLE_NAME)
@NamedQueries({
        @NamedQuery(name = Revision.REVISION_QUERY_PREFIX + "findAll",
                query = "select c from " + Revision.TABLE_NAME + " c"),
        @NamedQuery(name = Revision.REVISION_QUERY_PREFIX + "findByBranch",
                query = "select c from " + Revision.TABLE_NAME + " c where " + Dict.BRANCH_NAME + "=:" + Dict.BRANCH_NAME),
        @NamedQuery(name = Revision.REVISION_QUERY_PREFIX + "findLimitByBranch",
                query = "select c from " + Revision.TABLE_NAME + " c where " + Dict.BRANCH_NAME + "=:" + Dict.BRANCH_NAME
                        + " order by cast(c."+ Dict.REVISION_ID + " as long) desc"
        ),
        @NamedQuery(name = Revision.REVISION_QUERY_PREFIX + "findRevisionIDMax",
                query = "select max(cast(c." + Dict.REVISION_ID + " as long)) from " + Revision.TABLE_NAME + " c"
                        + " where " + Dict.BRANCH_NAME + "=:" + Dict.BRANCH_NAME
        )
})

public class Revision {
    public static final String TABLE_NAME = "Revision";
    public static final String REVISION_QUERY_PREFIX = "com.eco.revision.core.Revision.";

    @JsonProperty
    @NotEmpty
    @Column(name = Dict.ID)
    @Id
    private String id;

    @JsonProperty
    @NotEmpty
    @Column(name = Dict.BRANCH_NAME)
    private String branchName;

    @JsonProperty
    @NotEmpty
    @Column(name = Dict.REVISION_ID)
    private String revisionID;

    @JsonProperty
    @NotNull
    @Column(name = Dict.TIME)
    private Date time;

    @JsonProperty
    @NotEmpty
    @Column(name = Dict.AUTHOR)
    private String author;

    @JsonProperty
    @Column
    private String comment;

    @JsonProperty
    @Column
    private Date editTime;

    @JsonProperty
    @Column
    private String editor;

    @JsonProperty
    @Valid
    @Column
    private RevisionData data;


    public enum STATUS {
        COMMITTED(0)
        , SKIPPED(1)
        , DELETED(2);

        private final int value;
        private STATUS(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static STATUS toSTATUS(int value) {
            for (STATUS s : STATUS.values()) {
                if (value == s.getValue()) {
                    return s;
                }
            }

            return null;
        }
    }

    public Revision(String id, String branchName, String revisionID, Date time, String author,
                    String comment, String editor, Date editTime, RevisionData data) {
        this.id = id;
        this.branchName = branchName;
        this.revisionID = revisionID;
        this.time = time;
        this.author = author;
        this.comment = comment;
        this.editor = editor;
        this.editTime = editTime;
        this.data = data;
    }

    public Revision() {
    }

    public static Revision toRevision(String revisionJSON) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Revision revision = objectMapper.readValue(revisionJSON, Revision.class);

        return revision;
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

    public static String generateID(String branchName, String revisionID) {
        return branchName + revisionID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public RevisionData getData() {
        return data;
    }

    public void setData(RevisionData data) {
        this.data = data;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRevisionId() {
        return revisionID;
    }

    public void setRevisionId(String revisionID) {
        this.revisionID = revisionID;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static void main(String[] arg) {
        List<CommitStatus> commitStatuses = new ArrayList<>();
        commitStatuses.add(new CommitStatus("testBranch1", 0, "commitID1", "testComment1"));

        Revision testRevision = new Revision("testBranchtestRevision",
                "testBranch",
                "testRevision",
                new Date(123),
                "testAuthor",
                "testComment",
                "testEditor",
                new Date(456),
                new RevisionData(commitStatuses));

        System.out.println(testRevision.toString());
    }
}
