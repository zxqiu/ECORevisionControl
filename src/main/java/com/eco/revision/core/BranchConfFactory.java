package com.eco.revision.core;

import com.eco.svn.SVNConf;

import java.io.IOException;

/**
 * Created by neo on 8/31/18.
 */
public class BranchConfFactory {
    public enum TYPE {
        SVN(0);

        private int value;
        TYPE(int value) {
            value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static TYPE toTYPE(int value) {
            for (TYPE type : TYPE.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }

            return null;
        }
    }

    private static TYPE type = TYPE.SVN;

    public static BranchConf getBranchConf() throws IOException {
        switch (type) {
            case SVN:
                return SVNConf.getConf();
            default:
                break;
        }

        return SVNConf.getConf();
    }

    public static TYPE getType() {
        return type;
    }

    public static void setType(TYPE type) {
        BranchConfFactory.type = type;
    }
}
