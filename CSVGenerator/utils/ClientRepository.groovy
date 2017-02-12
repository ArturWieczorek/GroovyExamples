/**
 * Created by Artur Wieczorek on 11.02.17.
 */

package utils

import groovy.sql.Sql
@Grapes([
        @Grab(group='org.xerial', module='sqlite-jdbc', version='3.16.1'),
        @GrabConfig(systemClassLoader=true)
])
class ClientRepository {

    static Sql sql = Sql.newInstance('jdbc:sqlite:test.db', 'org.sqlite.JDBC')

    static List<String> getClientsId() {
        return sql.rows('SELECT client_number FROM clients')
                .stream()
                .map({row -> row.getProperty("client_number")})
                .collect()
    }

    static List<String> getClientsNumber(int... personalIdNumbers) {
        def idsAsString = personalIdNumbers.collect { "'$it'" }.join( ',' ) as String
        return sql.rows('SELECT client_number FROM clients WHERE personal_id IN (' + idsAsString + ')')
                .stream()
                .map({row -> row.getProperty("client_number")})
                .collect()
    }

    static void close(){
        sql.close()
    }

}
