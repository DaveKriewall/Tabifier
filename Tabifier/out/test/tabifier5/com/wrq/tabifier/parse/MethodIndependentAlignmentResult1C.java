public class MethodIndependentAlignmentTest1
{
    void callMethod(      Object o1      , Object oLonger2,
                          Object oLonger3, Object o4,
                          Object o5      , Object obj6,
                    final int    i       , int    j        );

    void method()
    {
        Object param1, param2, parm3, parameterNamed4IsALongOne, parameter5;
        callMethod(param1, param2,
                parameterNamed4IsALongOne, parm3,
                parameter5,                parm3,
                2,                         52    );
    }
}
