public final class RawOpenConnectionRequest
{
    public static final int BIG_ENDIAN    = 0x42;
    public static final int LITTLE_ENDIAN = 0x6c;
    public static final int MAJOR_VERSION =    2;
    public static final int MINOR_VERSION =    0;

    @CARD8                                    public         final int       byteOrder;
    @SuppressWarnings({"UnusedDeclaration"})
    @CARD8 @TRANSIENT("that.authData.length") private static final int       nAuthData = 0;
    @CARD16                                   public         final int       majorVersion;
    @CARD16                                   public         final int       minorVersion;
    @CARD16                                   public         final int       authDataQuads;
    @OBJECT_ARRAY("that.nAuthData")           public         final RawAUTH[] authData;

    public RawOpenConnectionRequest(int byteOrder,
                                    int authDataQuads,
                                    RawAUTH[] authData)
    {
        this.byteOrder     = byteOrder;
        majorVersion       = RawOpenConnectionRequest.MAJOR_VERSION;
        minorVersion       = RawOpenConnectionRequest.MINOR_VERSION;
        this.authDataQuads = authDataQuads;
        this.authData      = authData;
    }
}