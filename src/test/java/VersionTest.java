import java.lang.reflect.InvocationTargetException;

public class VersionTest {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        String value = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall", "Sisense");
        System.out.println(value);

    }


}
