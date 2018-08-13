package com.eco.utils.misc;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Serializer<T> 
{
	/**
     * Serialize the specified type into the specified DataOutputStream instance.
     * @param t type that needs to be serialized
     * @param dos DataOutput into which serialization needs to happen.
     * @throws IOException
     */
    public void serialize(T t, ObjectOutputStream oos) throws IOException;

    /**
     * Deserialize into the specified DataInputStream instance.
     * @param dis DataInput from which deserialization needs to happen.
     * @throws IOException
     * @return the type that was deserialized
     * @throws ClassNotFoundException 
     */
    public T deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException;
}


