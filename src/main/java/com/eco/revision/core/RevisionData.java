package com.eco.revision.core;

import com.eco.utils.misc.Dict;
import com.eco.utils.misc.Serializer;

import java.io.*;

/**
 * Created by neo on 8/12/18.
 */
public class RevisionData implements Serializable, Serializer<RevisionData> {
    static final long serialVersionUID = 529269941459L;

    private String comment;

    @Override
    public String toString() {
        return "["
                + Dict.COMMENT + ":" + comment
                + "]";
    }

    public RevisionData(RevisionData revisionData) {
        this.comment = revisionData.comment;
    }

    public RevisionData(InputStream is) throws IOException, ClassNotFoundException {
        RevisionData revisionData = deserialize((ObjectInputStream) is);
        new RevisionData(revisionData);
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        this.serialize(this, oos);
        return baos.toByteArray();
    }

    public void serialize(RevisionData revisionData, ObjectOutputStream oos) throws IOException {
        oos.writeObject(revisionData);
        oos.close();
    }

    public RevisionData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        RevisionData revisionData = (RevisionData) ois.readObject();
        ois.close();
        return revisionData;
    }
}
