package MangerApplyTest;

import com.demo.controllers.employer.EmployerApplyController;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

import static org.junit.jupiter.api.Assertions.*;

class EmployerJobControllerTest {

    private final EmployerApplyController controller = new EmployerApplyController(); // Bạn có thể mock nếu cần

    @Test
    void testJob_WithValidId_ReturnsCorrectViewAndModel() {
        ModelMap model = new ModelMap();
        int id = 5;

        String viewName = controller.job(id, model);

        System.out.println("✅ Mong muốn: view = employer/apply/job, id = 5");
        System.out.println("📤 Thực tế: view = " + viewName + ", id = " + model.get("id"));

        assertEquals("employer/apply/job", viewName);
        assertEquals(5, model.get("id"));
    }

    @Test
    void testJob_WithIdZero_ReturnsCorrectViewAndModel() {
        ModelMap model = new ModelMap();
        int id = 0;

        String viewName = controller.job(id, model);

        System.out.println("✅ Mong muốn: view = employer/apply/job, id = 0");
        System.out.println("📤 Thực tế: view = " + viewName + ", id = " + model.get("id"));

        assertEquals("employer/apply/job", viewName);
        assertEquals(0, model.get("id"));
    }

    @Test
    void testJob_WithNegativeId_ReturnsCorrectViewAndModel() {
        ModelMap model = new ModelMap();
        int id = -1;

        String viewName = controller.job(id, model);

        System.out.println("✅ Mong muốn: view = employer/apply/job, id = -1");
        System.out.println("📤 Thực tế: view = " + viewName + ", id = " + model.get("id"));

        assertEquals("employer/apply/job", viewName);
        assertEquals(-1, model.get("id"));
    }
}