public class AssignmentAlignmentTest4
{
    abstract int getYearDifference       (int start, int end);
    abstract int getRemainedDaysInTheYear(int start         );
    abstract int getElapsedDays          (int end           );

    int method(int aStartingDay, int aEndingDay)
    {
        int aRemainedDays, aYearDifference, aElapsedDays, aTotalDayDifference;

        aYearDifference     = getYearDifference       ( aStartingDay,
                                                        aEndingDay    );
        aRemainedDays       = getRemainedDaysInTheYear( aStartingDay  );
        aElapsedDays        = getElapsedDays          ( aEndingDay    );

        aTotalDayDifference = aRemainedDays + aElapsedDays;
        return aTotalDayDifference;
    }
}
