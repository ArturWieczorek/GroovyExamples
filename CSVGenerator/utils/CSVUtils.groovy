/**
 * Created by Artur Wieczorek on 11.02.17.
 */
package utils

import java.nio.file.Paths
import java.util.stream.Collectors

class CSVUtils {

    private static final String DEFAULT_SEPARATOR = ";"
    public static final String CSV_HEADER = "client_number;start_date;end_date;Triger_type;maximum_extension_term;name"

    private static StringBuffer buffer = new StringBuffer()

    static void writeLine(String clientNumber, String startDate, String endDate, String triggerType, String period, String promoName) throws IOException {
        buffer.append(Arrays.asList(clientNumber, startDate, endDate, triggerType, period, promoName)
                .stream()
                .collect(Collectors.joining(DEFAULT_SEPARATOR))
                .concat("\n")
        )
    }
	
    static void writeLine(List<String> values) throws IOException {
        buffer.append(values.stream().collect(Collectors.joining(DEFAULT_SEPARATOR)).concat("\n"))
    }
	

    static String extract() throws IOException {
        buffer.toString()
    }

    static void clearBuffer(){
	buffer.setLength(0)
    }
}

