import java.text.ParseException
import java.text.SimpleDateFormat

def df = new SimpleDateFormat("MM/dd/yy")
try {
    pUser.birthdate = df.parse(attribute.value)
} catch (ParseException parseException) {
    println(" Failed to parse birth date: " + parseException.getMessage())
}
output = ""