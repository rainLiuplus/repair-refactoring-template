package sg.edu.nus.se.its.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.javatuples.Pair;

/**
 * Custom deserializer for pairs.
 */
public class PairDeserializer<K, V> implements JsonDeserializer<Pair<K, V>> {
  private Class<K> typeOfK;
  private Class<V> typeOfV;

  public PairDeserializer(Class<K> typeOfK, Class<V> typeOfV) {
    this.typeOfK = typeOfK;
    this.typeOfV = typeOfV;
  }

  @Override
  public Pair<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonArray tupleValues = json.getAsJsonArray();
    K key = context.deserialize(tupleValues.get(0), typeOfK);
    V val = context.deserialize(tupleValues.get(1), typeOfV);
    return Pair.with(key, val);
  }
}

