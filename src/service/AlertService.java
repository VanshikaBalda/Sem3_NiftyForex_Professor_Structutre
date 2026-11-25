
package forex.service;

import forex.dao.AlertDAO;
import java.math.BigDecimal;
import java.util.*;

public class AlertService {

    private final AlertDAO dao = new AlertDAO();

    public void add(int uid, String c, BigDecimal t, String d) {

        if (t == null || t.signum() <= 0) {
            throw new IllegalArgumentException("Target must be positive.");
        }

        if (d == null ||
            (!d.equalsIgnoreCase("ABOVE") &&
             !d.equalsIgnoreCase("BELOW"))) {

            throw new IllegalArgumentException(
                "Direction must be ABOVE or BELOW."
            );
        }

        dao.add(uid, c, t, d);
    }

    public List<String> list(int uid) {
        return dao.list(uid);
    }
}
