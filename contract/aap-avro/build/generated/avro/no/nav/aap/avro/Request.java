/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package no.nav.aap.avro;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Request extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 8894769181437067508L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Request\",\"namespace\":\"no.nav.aap.avro\",\"fields\":[{\"name\":\"mottattDato\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"ytelse\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"arbeidetUtenlands\",\"type\":\"boolean\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Request> ENCODER =
      new BinaryMessageEncoder<Request>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Request> DECODER =
      new BinaryMessageDecoder<Request>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Request> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Request> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Request> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Request>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Request to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Request from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Request instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Request fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.String mottattDato;
  private java.lang.String ytelse;
  private boolean arbeidetUtenlands;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Request() {}

  /**
   * All-args constructor.
   * @param mottattDato The new value for mottattDato
   * @param ytelse The new value for ytelse
   * @param arbeidetUtenlands The new value for arbeidetUtenlands
   */
  public Request(java.lang.String mottattDato, java.lang.String ytelse, java.lang.Boolean arbeidetUtenlands) {
    this.mottattDato = mottattDato;
    this.ytelse = ytelse;
    this.arbeidetUtenlands = arbeidetUtenlands;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return mottattDato;
    case 1: return ytelse;
    case 2: return arbeidetUtenlands;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: mottattDato = value$ != null ? value$.toString() : null; break;
    case 1: ytelse = value$ != null ? value$.toString() : null; break;
    case 2: arbeidetUtenlands = (java.lang.Boolean)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'mottattDato' field.
   * @return The value of the 'mottattDato' field.
   */
  public java.lang.String getMottattDato() {
    return mottattDato;
  }


  /**
   * Sets the value of the 'mottattDato' field.
   * @param value the value to set.
   */
  public void setMottattDato(java.lang.String value) {
    this.mottattDato = value;
  }

  /**
   * Gets the value of the 'ytelse' field.
   * @return The value of the 'ytelse' field.
   */
  public java.lang.String getYtelse() {
    return ytelse;
  }


  /**
   * Sets the value of the 'ytelse' field.
   * @param value the value to set.
   */
  public void setYtelse(java.lang.String value) {
    this.ytelse = value;
  }

  /**
   * Gets the value of the 'arbeidetUtenlands' field.
   * @return The value of the 'arbeidetUtenlands' field.
   */
  public boolean getArbeidetUtenlands() {
    return arbeidetUtenlands;
  }


  /**
   * Sets the value of the 'arbeidetUtenlands' field.
   * @param value the value to set.
   */
  public void setArbeidetUtenlands(boolean value) {
    this.arbeidetUtenlands = value;
  }

  /**
   * Creates a new Request RecordBuilder.
   * @return A new Request RecordBuilder
   */
  public static no.nav.aap.avro.Request.Builder newBuilder() {
    return new no.nav.aap.avro.Request.Builder();
  }

  /**
   * Creates a new Request RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Request RecordBuilder
   */
  public static no.nav.aap.avro.Request.Builder newBuilder(no.nav.aap.avro.Request.Builder other) {
    if (other == null) {
      return new no.nav.aap.avro.Request.Builder();
    } else {
      return new no.nav.aap.avro.Request.Builder(other);
    }
  }

  /**
   * Creates a new Request RecordBuilder by copying an existing Request instance.
   * @param other The existing instance to copy.
   * @return A new Request RecordBuilder
   */
  public static no.nav.aap.avro.Request.Builder newBuilder(no.nav.aap.avro.Request other) {
    if (other == null) {
      return new no.nav.aap.avro.Request.Builder();
    } else {
      return new no.nav.aap.avro.Request.Builder(other);
    }
  }

  /**
   * RecordBuilder for Request instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Request>
    implements org.apache.avro.data.RecordBuilder<Request> {

    private java.lang.String mottattDato;
    private java.lang.String ytelse;
    private boolean arbeidetUtenlands;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(no.nav.aap.avro.Request.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.mottattDato)) {
        this.mottattDato = data().deepCopy(fields()[0].schema(), other.mottattDato);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.ytelse)) {
        this.ytelse = data().deepCopy(fields()[1].schema(), other.ytelse);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.arbeidetUtenlands)) {
        this.arbeidetUtenlands = data().deepCopy(fields()[2].schema(), other.arbeidetUtenlands);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing Request instance
     * @param other The existing instance to copy.
     */
    private Builder(no.nav.aap.avro.Request other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.mottattDato)) {
        this.mottattDato = data().deepCopy(fields()[0].schema(), other.mottattDato);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.ytelse)) {
        this.ytelse = data().deepCopy(fields()[1].schema(), other.ytelse);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.arbeidetUtenlands)) {
        this.arbeidetUtenlands = data().deepCopy(fields()[2].schema(), other.arbeidetUtenlands);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'mottattDato' field.
      * @return The value.
      */
    public java.lang.String getMottattDato() {
      return mottattDato;
    }


    /**
      * Sets the value of the 'mottattDato' field.
      * @param value The value of 'mottattDato'.
      * @return This builder.
      */
    public no.nav.aap.avro.Request.Builder setMottattDato(java.lang.String value) {
      validate(fields()[0], value);
      this.mottattDato = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'mottattDato' field has been set.
      * @return True if the 'mottattDato' field has been set, false otherwise.
      */
    public boolean hasMottattDato() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'mottattDato' field.
      * @return This builder.
      */
    public no.nav.aap.avro.Request.Builder clearMottattDato() {
      mottattDato = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'ytelse' field.
      * @return The value.
      */
    public java.lang.String getYtelse() {
      return ytelse;
    }


    /**
      * Sets the value of the 'ytelse' field.
      * @param value The value of 'ytelse'.
      * @return This builder.
      */
    public no.nav.aap.avro.Request.Builder setYtelse(java.lang.String value) {
      validate(fields()[1], value);
      this.ytelse = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'ytelse' field has been set.
      * @return True if the 'ytelse' field has been set, false otherwise.
      */
    public boolean hasYtelse() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'ytelse' field.
      * @return This builder.
      */
    public no.nav.aap.avro.Request.Builder clearYtelse() {
      ytelse = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'arbeidetUtenlands' field.
      * @return The value.
      */
    public boolean getArbeidetUtenlands() {
      return arbeidetUtenlands;
    }


    /**
      * Sets the value of the 'arbeidetUtenlands' field.
      * @param value The value of 'arbeidetUtenlands'.
      * @return This builder.
      */
    public no.nav.aap.avro.Request.Builder setArbeidetUtenlands(boolean value) {
      validate(fields()[2], value);
      this.arbeidetUtenlands = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'arbeidetUtenlands' field has been set.
      * @return True if the 'arbeidetUtenlands' field has been set, false otherwise.
      */
    public boolean hasArbeidetUtenlands() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'arbeidetUtenlands' field.
      * @return This builder.
      */
    public no.nav.aap.avro.Request.Builder clearArbeidetUtenlands() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Request build() {
      try {
        Request record = new Request();
        record.mottattDato = fieldSetFlags()[0] ? this.mottattDato : (java.lang.String) defaultValue(fields()[0]);
        record.ytelse = fieldSetFlags()[1] ? this.ytelse : (java.lang.String) defaultValue(fields()[1]);
        record.arbeidetUtenlands = fieldSetFlags()[2] ? this.arbeidetUtenlands : (java.lang.Boolean) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Request>
    WRITER$ = (org.apache.avro.io.DatumWriter<Request>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Request>
    READER$ = (org.apache.avro.io.DatumReader<Request>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.mottattDato);

    out.writeString(this.ytelse);

    out.writeBoolean(this.arbeidetUtenlands);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.mottattDato = in.readString();

      this.ytelse = in.readString();

      this.arbeidetUtenlands = in.readBoolean();

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.mottattDato = in.readString();
          break;

        case 1:
          this.ytelse = in.readString();
          break;

        case 2:
          this.arbeidetUtenlands = in.readBoolean();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










