package com.eco.Revision.core;

import com.eco.utils.misc.Serializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by neo on 8/12/18.
 */
public class RevisionData implements Serializable, Serializer<RevisionData> {
    static final long serialVersionUID = 529269941459L;

    private String comment;

    @Override
    public String toString() {
        return "[";
                + Dict.COMMENT + ":" + comment
                + "]";
    }

    public RevisionData(String comment) {
        this.comment = comment;
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
}
