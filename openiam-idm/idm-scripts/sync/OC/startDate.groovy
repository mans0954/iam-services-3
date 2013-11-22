import java.text.ParseException
import java.text.SimpleDateFormat

output = ""

def df = new SimpleDateFormat("MM/dd/yy")
try {
    pUser.startDate = df.parse(attribute.value)
} catch (ParseException parseException) {
    println(" Failed to parse birth date: " + parseException.getMessage())
}