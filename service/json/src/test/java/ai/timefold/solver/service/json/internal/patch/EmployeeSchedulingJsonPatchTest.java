package ai.timefold.solver.service.json.internal.patch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EmployeeSchedulingJsonPatchTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testAddEmployee() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/-", "value": {"id": "john doe","contracts": [
                                "fullTimeContract"
                            ],
                            "skills": [
                                {
                                    "id": "Ambulance"
                                }
                            ]} }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees + 1);

        JsonNode addedEmployee = modelInputPatched.get("employees").get(numberOfEmployeesAfterPatch - 1);
        assertThat(addedEmployee.get("id").asText()).isEqualTo("john doe");
        assertThat(addedEmployee.get("contracts").get(0).asText()).isEqualTo("fullTimeContract");
        assertThat(addedEmployee.get("skills").get(0).get("id").asText()).isEqualTo("Ambulance");
    }

    @Test
    public void testAddContractToEmployee() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/[id=Ann Cole]/contracts/-", "value": "test" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        JsonNode employee = modelInputPatched.get("employees").get(0);
        assertThat(employee.get("contracts").get(0).asText()).isEqualTo("fullTimeContract");
        assertThat(employee.get("contracts").get(1).asText()).isEqualTo("test");
    }

    @Test
    public void testAddUnavailableTimeSpansToEmployee() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/[id=Ann Cole]/unavailableTimeSpans/-", "value": {
                         "start": "2027-02-01T00:00:00Z",
                         "end": "2027-02-02T00:00:00Z"
                       } }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        JsonNode employee = modelInputPatched.get("employees").get(0);
        assertThat(employee.get("unavailableTimeSpans").size()).isEqualTo(1);
    }

    @Test
    public void testRemoveContractFromEmployee() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/employees/[id=Ann Cole]/contracts/0" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        JsonNode employee = modelInputPatched.get("employees").get(0);
        assertThat(employee.get("contracts").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveContractFromEmployeeByName() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/employees/[id=Ann Cole]/contracts/[fullTimeContract]" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        JsonNode employee = modelInputPatched.get("employees").get(0);
        assertThat(employee.get("contracts").size()).isEqualTo(0);
    }

    @Test
    public void testAddSkillEmployee() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/[id=Ann Cole]/skills/-", "value": {"id": "Test"}}
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        ObjectNode emp = (ObjectNode) modelInputPatched.at("/employees/0");
        ArrayNode skills = (ArrayNode) emp.get("skills");
        assertThat(skills.size()).isEqualTo(2);

        List<String> skillIds = skills.valueStream().map(item -> item.get("id").asText()).toList();
        assertThat(skillIds).containsExactlyElementsOf(List.of("Ambulance", "Test"));
    }

    @Test
    public void testAddSkillEmployeeNotExisting() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/[id=Not existing]/skills/-", "value": {"id": "Test"}}
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

    }

    @Test
    public void testAddPreferedTimeSpanForEmployee() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/[id=Ann Cole]/preferredTimeSpans/-", "value": {
                  "start" : "2025-08-05T00:00:00-04:00",
                  "end" : "2025-08-07T00:00:00-04:00",
                  "includeShiftTags" : [ "Night" ]
                }}
                              ]
                              """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        ObjectNode emp = (ObjectNode) modelInputPatched.at("/employees/0");
        ArrayNode preferredTimeSpans = (ArrayNode) emp.get("preferredTimeSpans");
        assertThat(preferredTimeSpans.size()).isEqualTo(3);
    }

    @Test
    public void testRemoveSkillFromEmployee() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/employees/[id=Ann Cole]/skills/0"}
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        ObjectNode emp = (ObjectNode) modelInputPatched.at("/employees/0");
        ArrayNode skills = (ArrayNode) emp.get("skills");
        assertThat(skills.size()).isEqualTo(0);
    }

    @Test
    public void testRemoveSkillFromEmployeeBySkillId() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/employees/[id=Ann Cole]/skills/[id=Ambulance]"}
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        ObjectNode emp = (ObjectNode) modelInputPatched.at("/employees/0");
        ArrayNode skills = (ArrayNode) emp.get("skills");
        assertThat(skills.size()).isEqualTo(0);
    }

    @Test
    public void testReplaceSkillEmployee() throws IOException {

        String patch = """
                [
                  { "op": "replace", "path": "/employees/[id=Ann Cole]/skills", "value": [{"id": "Test"}]}
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        ObjectNode emp = (ObjectNode) modelInputPatched.at("/employees/0");
        ArrayNode skills = (ArrayNode) emp.get("skills");
        assertThat(skills.size()).isEqualTo(1);

        assertThat(skills.get(0).get("id").textValue()).isEqualTo("Test");
    }

    @Test
    public void testReplaceTimeZoneForEmployee() throws IOException {

        String patch = """
                [
                  { "op": "replace", "path": "/employees/[id=Ann Cole]/timeZoneId", "value": "-08:00"}
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees);

        ObjectNode emp = (ObjectNode) modelInputPatched.at("/employees/0");
        String timezoneId = emp.get("timeZoneId").textValue();
        assertThat(timezoneId).isEqualTo("-08:00");
    }

    @Test
    public void testRemoveEmployeeWithSkill() throws IOException {
        String patch = """
                [
                { "op": "remove", "path": "/employees/[skills/id=Ambulance]" }
                ]
                """;

        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees - 5);
    }

    @Test
    public void testRemoveEmployeeWithId() throws IOException {
        String patch = """
                [
                { "op": "remove", "path": "/employees/[id=Ann Cole]" }
                ]
                """;

        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees - 1);
    }

    @Test
    public void testRemoveEmployeeByIndex() throws IOException {
        String patch = """
                [
                { "op": "remove", "path": "/employees/0" }
                ]
                """;

        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfEmployees = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfEmployees - 1);
    }

    @Test
    public void testAddShift() throws IOException {

        String patch = """
                        [
                          { "op": "add", "path": "/shifts/-", "value": {
                    "id": "Sun M Ambulance",
                    "start": "2025-08-10T06:00:00-04:00",
                    "end": "2025-08-10T14:00:00-04:00",
                    "requiredSkills": [
                        "Ambulance"
                    ],
                    "tags": [
                        "Morning"
                    ]
                } }
                        ]
                        """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("shifts").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfShiftsAfterPatch = modelInputPatched.get("shifts").size();

        assertThat(numberOfShiftsAfterPatch).isEqualTo(numberOfShifts + 1);

        JsonNode addedShift = modelInputPatched.get("shifts").get(numberOfShiftsAfterPatch - 1);
        assertThat(addedShift.get("id").asText()).isEqualTo("Sun M Ambulance");
        assertThat(addedShift.get("requiredSkills").get(0).asText()).isEqualTo("Ambulance");
        assertThat(addedShift.get("tags").get(0).asText()).isEqualTo("Morning");
    }

    @Test
    public void testAddTagToShift() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/shifts/[id=Mon M Ambulance]/tags/-", "value": "Night" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("shifts").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("shifts").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfShifts);

        JsonNode shifts = modelInputPatched.get("shifts").get(0);
        assertThat(shifts.get("tags").size()).isEqualTo(2);
        assertThat(shifts.get("tags").get(0).asText()).isEqualTo("Morning");
        assertThat(shifts.get("tags").get(1).asText()).isEqualTo("Night");
    }

    @Test
    public void testAddTagToEmployee() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/employees/[id=Ann Cole]/tags/-", "value": "Night" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("employees").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("employees").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfShifts);

        JsonNode tags = modelInputPatched.get("employees").get(0);
        assertThat(tags.get("tags").size()).isEqualTo(1);
        assertThat(tags.get("tags").get(0).asText()).isEqualTo("Night");

        patch = """
                [
                  { "op": "replace", "path": "/employees/[id=Ann Cole]/tags", "value": ["Day"] }
                ]
                """;

        modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        tags = modelInputPatched.get("employees").get(0);
        assertThat(tags.get("tags").size()).isEqualTo(1);
        assertThat(tags.get("tags").get(0).asText()).isEqualTo("Day");
    }

    @Test
    public void testAddRequiredSkillsToShift() throws IOException {

        String patch = """
                [
                  { "op": "add", "path": "/shifts/[id=Mon M Ambulance]/requiredSkills/-", "value": "Surgery" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("shifts").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("shifts").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfShifts);

        JsonNode shifts = modelInputPatched.get("shifts").get(0);
        assertThat(shifts.get("requiredSkills").size()).isEqualTo(2);
        assertThat(shifts.get("requiredSkills").get(0).asText()).isEqualTo("Ambulance");
        assertThat(shifts.get("requiredSkills").get(1).asText()).isEqualTo("Surgery");
    }

    @Test
    public void testRemoveRequiredSkillsFromShift() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/shifts/[id=Mon M Ambulance]/requiredSkills/0" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("shifts").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("shifts").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfShifts);

        JsonNode shifts = modelInputPatched.get("shifts").get(0);
        assertThat(shifts.get("requiredSkills").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveRequiredSkillsFromShiftByName() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/shifts/[id=Mon M Ambulance]/requiredSkills/Ambulance" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("shifts").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("shifts").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfShifts);

        JsonNode shifts = modelInputPatched.get("shifts").get(0);
        assertThat(shifts.get("requiredSkills").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveShiftById() throws IOException {

        String patch = """
                [
                  { "op": "remove", "path": "/shifts/[id=Mon M Ambulance]" }
                ]
                """;
        JsonNode modelInput = mapper.readTree(
                this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/CONTRACT_RULES_input.json"));

        int numberOfShifts = modelInput.get("shifts").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfEmployeesAfterPatch = modelInputPatched.get("shifts").size();

        assertThat(numberOfEmployeesAfterPatch).isEqualTo(numberOfShifts - 1);
    }
}
