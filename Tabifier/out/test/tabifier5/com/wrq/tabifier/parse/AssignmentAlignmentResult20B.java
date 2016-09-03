public class AssignmentAlignmentTest20 {


    private static final String SQL_GET_COMPANY_NAMES       = "select name, id from company order by name";
    private static final String SQL_GET_COMPANY_STATUS      = "select name, id from company_status order by name";
    private static final String SQL_GET_CONTACT_STATUS      = "select name, id from contact_status order by name";
    private static final String SQL_GET_EMPLOYEE            = "select name, id from employee order by name";
    private static final String SQL_GET_PASSWORD            = "select password from employee where name = ?";

    private static final String SQL_GET_CONTACT             = "select cu.id, co.fname, co.lname, co.title, cu.description, co.company_id, " +
                                                              "co.email, co.contact_status_id, co.next_call, co.next_call_objective, "      +
                                                              "cu.areacode, cu.phone, cu.created_by, cu.created_on, "                       +
                                                              "a.street1, a.street2, a.city, a.state, a.zip "                               +
                                                              "from contact co, customer cu, address a "                                    +
                                                              "where co.id = cu.id "                                                        +
                                                              "and cu.address_id = a .id ";
    private static final String SQL_GET_CONTACT_BY_ID       =
            SQL_GET_CONTACT                                                       +
            "and co.id = ? ";
    private static final String SQL_GET_CONTACTS_BY_COMPANY =
            SQL_GET_CONTACT                                                       +
            "and co.company_id = ?";

    private static final String SQL_GET_COMPANY_BY_ID       =
            "select cu.id, co.name, cu.description, co.owner_id, co.environment," +
            "co.company_status_id, cu.areacode, cu.phone, cu.created_by, "        +
            "cu.created_on, a.street1, a.street2, a.city, a.state, a.zip "        +
            "from customer cu, company co, address a "                            +
            "where co.id = cu.id "                                                +
            "and cu.address_id = a.id "                                           +
            "and cu.id = ?";
    private static final String SQL_GET_NOTES               =
            "select id, information, customer_id, created, created_by "           +
            "from note where customer_id = ? "                                    +
            "order by created descending";
    private static final String SQL_GET_SECONDARY_ADDRESSES = "";

    private static final String SQL_CREATE_CONTACT          = "insert into contact values (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_CREATE_COMPANY          = "insert into company values (?,?,?,?,?)";
    private static final String SQL_CREATE_CUSTOMER         = "insert into customer values (null,?,?,?,?,?,?)";
    private static final String SQL_CREATE_ADDRESS          = "insert into address values (null,?,?,?,?,?)";
    private static final String SQL_CREATE_NOTE             = "insert into note values (null,?,?,?,?)";

}
