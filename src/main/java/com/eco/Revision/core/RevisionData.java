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

    public void serialize(RevisionData revisionData, ObjectOutputStream oos) throws IOException {

    }

    public RevisionData deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return null;
    }
}
