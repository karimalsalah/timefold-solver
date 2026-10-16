package ai.timefold.solver.service.json.internal.patch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class FieldServiceRoutingJsonPatchTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testAddVehicle() throws IOException {

        String patch = """
                [
                          { "op": "add", "path": "/vehicles/-", "value": {
                    "id": "AnnExtra",
                    "vehicleType": "VAN",
                    "shifts": [
                        {
                            "id": "cd4c29ed",
                            "startLocation": [
                                33.771758735816796,
                                -84.37174927400983
                            ],
                            "minStartTime": "2025-08-18T08:00:00-04:00",
                            "maxEndTime": "2025-08-18T17:00:00-04:00",
                            "skills": [
                                {
                                    "name": "electrician",
                                    "level": 1,
                                    "multiplier": null
                                }
                            ],
                            "tags": [
                            ],
                            "requiredBreaks": [
                                {
                                    "id": "cd4c29ed Lunch",
                                    "minStartTime": "2025-08-18T12:00:00-04:00",
                                    "maxEndTime": null,
                                    "duration": "PT1H",
                                    "costImpact": "PAID",
                                    "type": "FLOATING"
                                }
                            ],
                            "temporarySkillSets": [
                            ],
                            "temporaryTagSets": [
                            ],
                            "itinerary": [
                            ]
                        }
                    ],
                    "historicalTimeUtilized": "PT0S",
                    "historicalTimeCapacity": "PT0S"
                } }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles + 1);

        JsonNode addedVehicle = modelInputPatched.get("vehicles").get(numberOfVehiclesAfterPatch - 1);
        assertThat(addedVehicle.get("id").asText()).isEqualTo("AnnExtra");
        assertThat(addedVehicle.get("vehicleType").asText()).isEqualTo("VAN");
    }

    @Test
    public void testAddShiftToVehicle() throws IOException {

        String patch = """
                [
                     { "op": "add", "path": "/vehicles/[id=Ann]/shifts/-", "value": {
                            "id": "cd4c29ex",
                            "startLocation": [
                                33.771758735816796,
                                -84.37174927400983
                            ],
                            "minStartTime": "2025-08-18T08:00:00-04:00",
                            "maxEndTime": "2025-08-18T17:00:00-04:00",
                            "skills": [
                                {
                                    "name": "electrician",
                                    "level": 1,
                                    "multiplier": null
                                }
                            ],
                            "tags": [
                            ],
                            "requiredBreaks": [
                                {
                                    "id": "cd4c29ed Lunch",
                                    "minStartTime": "2025-08-18T12:00:00-04:00",
                                    "maxEndTime": null,
                                    "duration": "PT1H",
                                    "costImpact": "PAID",
                                    "type": "FLOATING"
                                }
                            ],
                            "temporarySkillSets": [
                            ],
                            "temporaryTagSets": [
                            ],
                            "itinerary": [
                            ]
                        }}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.get("shifts").size()).isEqualTo(2);

    }

    @Test
    public void testAddTagToVehicleByShiftIndex() throws IOException {

        String patch = """
                [
                     { "op": "add", "path": "/vehicles/[id=Ann]/shifts/0/tags/-", "value": "emergency"}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/tags").size()).isEqualTo(1);
        assertThat(vehicle.at("/shifts/0/tags").get(0).asText()).isEqualTo("emergency");

    }

    @Test
    public void testAddTagToVehicleByShiftId() throws IOException {

        String patch = """
                [
                     { "op": "add", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]/tags/-", "value": "emergency"}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/tags").size()).isEqualTo(1);
        assertThat(vehicle.at("/shifts/0/tags").get(0).asText()).isEqualTo("emergency");

    }

    @Test
    public void testAddSkillToVehicleByShiftId() throws IOException {

        String patch = """
                [
                     { "op": "add", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]/skills/-", "value": {
                                    "name": "musician",
                                    "level": 1,
                                    "multiplier": null
                                }}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/skills").size()).isEqualTo(2);
        assertThat(vehicle.at("/shifts/0/skills").get(0).get("name").asText()).isEqualTo("electrician");
        assertThat(vehicle.at("/shifts/0/skills").get(1).get("name").asText()).isEqualTo("musician");

    }

    @Test
    public void testReplaceSkillsForVehicleByShiftId() throws IOException {

        String patch = """
                [
                     { "op": "replace", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]/skills", "value": [{
                                    "name": "musician",
                                    "level": 1,
                                    "multiplier": null
                                }]}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/skills").size()).isEqualTo(1);
        assertThat(vehicle.at("/shifts/0/skills").get(0).get("name").asText()).isEqualTo("musician");

    }

    @Test
    public void testReplaceRequiredBreaksForVehicleByShiftId() throws IOException {

        String patch = """
                [
                     { "op": "replace", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]/requiredBreaks", "value": [
                                {
                                    "id": "cd4c29ed Lunch",
                                    "minStartTime": "2025-08-18T12:00:00-04:00",
                                    "maxEndTime": null,
                                    "duration": "PT1H",
                                    "costImpact": "PAID",
                                    "type": "FLOATING"
                                },
                                {
                                    "id": "cd4c29ed Dinner",
                                    "minStartTime": "2025-08-18T18:00:00-04:00",
                                    "maxEndTime": null,
                                    "duration": "PT1H",
                                    "costImpact": "PAID",
                                    "type": "FLOATING"
                                }
                            ]}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/requiredBreaks").size()).isEqualTo(2);
        assertThat(vehicle.at("/shifts/0/requiredBreaks").get(0).get("id").asText()).isEqualTo("cd4c29ed Lunch");
        assertThat(vehicle.at("/shifts/0/requiredBreaks").get(1).get("id").asText()).isEqualTo("cd4c29ed Dinner");
    }

    @Test
    public void testRemoveRequiredBreaksForVehicleByShiftId() throws IOException {

        String patch = """
                [
                     { "op": "remove", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]/requiredBreaks/[id=cd4c29ed Lunch]"}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/requiredBreaks").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveRequiredBreaksForVehicleByShiftIdIndex() throws IOException {

        String patch = """
                [
                     { "op": "remove", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]/requiredBreaks/0"}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts/0/requiredBreaks").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveShiftForVehicleByShiftId() throws IOException {

        String patch = """
                [
                     { "op": "remove", "path": "/vehicles/[id=Ann]/shifts/[id=cd4c29ed]"}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveShiftForVehicleByIndex() throws IOException {

        String patch = """
                [
                     { "op": "remove", "path": "/vehicles/[id=Ann]/shifts/0"}
                ]

                """;

        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVehicles = modelInput.get("vehicles").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);
        int numberOfVehiclesAfterPatch = modelInputPatched.get("vehicles").size();

        assertThat(numberOfVehiclesAfterPatch).isEqualTo(numberOfVehicles);

        JsonNode vehicle = modelInputPatched.get("vehicles").get(0);
        assertThat(vehicle.at("/shifts").size()).isEqualTo(0);
    }

    @Test
    public void testAddVisit() throws IOException {

        String patch = """
                [
                          { "op": "add", "path": "/visits/-", "value": {
                        "id": "6e57fcdx",
                        "name": "Cole Inc. extra",
                        "location": [
                            33.68906251585709,
                            -84.44268080179887
                        ],
                        "timeWindows": [
                            {
                                "minStartTime": "2025-08-18T09:00:00-04:00",
                                "maxEndTime": "2025-08-18T17:00:00-04:00"
                            }
                        ],
                        "serviceDuration": "PT1H15M",
                        "requiredSkills": [
                            {
                                "name": "plumber",
                                "minLevel": null
                            }
                        ],
                        "priority": "10",
                        "pinningRequested": false
                    } }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits + 1);

        JsonNode addedVisit = modelInputPatched.get("visits").get(numberOfVisitsAfterPatch - 1);
        assertThat(addedVisit.get("id").asText()).isEqualTo("6e57fcdx");
        assertThat(addedVisit.get("name").asText()).isEqualTo("Cole Inc. extra");
    }

    @Test
    public void testAddVisitsRequiredSkill() throws IOException {

        String patch = """
                [
                          { "op": "add", "path": "/visits/[id=6e57fcd4]/requiredSkills/-", "value": {
                                "name": "electrician",
                                "minLevel": null
                            }
                          }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        JsonNode visit = modelInputPatched.get("visits").get(0);
        assertThat(visit.at("/requiredSkills").size()).isEqualTo(2);
        assertThat(visit.at("/requiredSkills").get(0).get("name").asText()).isEqualTo("plumber");
        assertThat(visit.at("/requiredSkills").get(1).get("name").asText()).isEqualTo("electrician");
    }

    @Test
    public void testRemoveVisitsRequiredSkill() throws IOException {

        String patch = """
                [
                          { "op": "remove", "path": "/visits/[id=6e57fcd4]/requiredSkills/[name=plumber]"}
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        JsonNode visit = modelInputPatched.get("visits").get(0);
        assertThat(visit.at("/requiredSkills").size()).isEqualTo(0);
    }

    @Test
    public void testRemoveVisitsById() throws IOException {

        String patch = """
                [
                          { "op": "remove", "path": "/visits/[id=6e57fcd4]"}
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits - 1);
    }

    @Test
    public void testReplaceVisitsById() throws IOException {

        String patch = """
                [
                          { "op": "replace", "path": "/visits/[id=6e57fcd4]", "value": {
                        "id": "6e57fcdx",
                        "name": "Cole Inc. extra",
                        "location": [
                            33.68906251585709,
                            -84.44268080179887
                        ],
                        "timeWindows": [
                            {
                                "minStartTime": "2025-08-18T09:00:00-04:00",
                                "maxEndTime": "2025-08-18T17:00:00-04:00"
                            }
                        ],
                        "serviceDuration": "PT1H15M",
                        "requiredSkills": [
                            {
                                "name": "plumber",
                                "minLevel": null
                            }
                        ],
                        "priority": "10",
                        "pinningRequested": false
                    }}

                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        JsonNode addedVisit = modelInputPatched.get("visits").get(numberOfVisitsAfterPatch - 1);
        assertThat(addedVisit.get("id").asText()).isEqualTo("6e57fcdx");
        assertThat(addedVisit.get("name").asText()).isEqualTo("Cole Inc. extra");
    }

    @Test
    public void testReplaceVisitsByIndex() throws IOException {

        String patch = """
                [
                          { "op": "replace", "path": "/visits/0", "value": {
                        "id": "6e57fcdx",
                        "name": "Cole Inc. extra",
                        "location": [
                            33.68906251585709,
                            -84.44268080179887
                        ],
                        "timeWindows": [
                            {
                                "minStartTime": "2025-08-18T09:00:00-04:00",
                                "maxEndTime": "2025-08-18T17:00:00-04:00"
                            }
                        ],
                        "serviceDuration": "PT1H15M",
                        "requiredSkills": [
                            {
                                "name": "plumber",
                                "minLevel": null
                            }
                        ],
                        "priority": "10",
                        "pinningRequested": false
                    }}

                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        JsonNode addedVisit = modelInputPatched.get("visits").get(0);
        assertThat(addedVisit.get("id").asText()).isEqualTo("6e57fcdx");
        assertThat(addedVisit.get("name").asText()).isEqualTo("Cole Inc. extra");
    }

    @Test
    public void testReplaceVisitsPriorityById() throws IOException {

        String patch = """
                [
                          { "op": "replace", "path": "/visits/[id=6e57fcd4]/priority", "value": "5"}

                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        JsonNode visit = modelInputPatched.get("visits").get(0);
        assertThat(visit.get("priority").asText()).isEqualTo("5");
    }

    @Test
    public void testReplaceVisitsLocationById() throws IOException {

        String patch = """
                [
                          { "op": "replace", "path": "/visits/[id=6e57fcd4]/location", "value": [
                            50.68906251585709,
                            -90.44268080179887
                        ]}

                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        JsonNode visit = modelInputPatched.get("visits").get(0);
        assertThat(visit.get("location").get(0).asText()).isEqualTo("50.68906251585709");
        assertThat(visit.get("location").get(1).asText()).isEqualTo("-90.44268080179887");
    }

    @Test
    public void testReplaceVisitsPinnedResourceByPriority() throws IOException {

        String patch = """
                [
                          { "op": "replace", "path": "/visits/[priority=6]/pinningRequested", "value": true}

                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisits = modelInput.get("visits").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitsAfterPatch = modelInputPatched.get("visits").size();

        assertThat(numberOfVisitsAfterPatch).isEqualTo(numberOfVisits);

        List<Boolean> pinnedRequestedForPriority6 =
                modelInputPatched.get("visits").valueStream().filter(item -> item.get("priority").asText().equals("6"))
                        .map(item -> item.get("pinningRequested").asBoolean()).toList();
        assertThat(pinnedRequestedForPriority6.size()).isEqualTo(10);
        assertThat(pinnedRequestedForPriority6.stream().allMatch(v -> v == true)).isTrue();
    }

    @Test
    public void testAddVisitGroup() throws IOException {

        String patch = """
                        [
                                  { "op": "add", "path": "/visitGroups/-", "value": {
                    "id": "ce9aa5ex",
                    "alignment": "START",
                    "serviceDurationStrategy": "INDIVIDUAL",
                    "visits": [
                        {
                            "id": "0d7f6e52",
                            "name": "Jay Robinson 1 / 3",
                            "location": [
                                33.80118537771744,
                                -84.40335014628359
                            ],
                            "timeWindows": [
                                {
                                    "minStartTime": "2025-08-18T08:00:00-04:00",
                                    "maxEndTime": "2025-08-18T16:00:00-04:00"
                                }
                            ],
                            "serviceDuration": "PT1H30M",
                            "requiredSkills": [
                                {
                                    "name": "plumber",
                                    "minLevel": null
                                }
                            ],
                            "priority": "8",
                            "pinningRequested": false
                        }
                    ]
                } }
                        ]
                        """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisitGroups = modelInput.get("visitGroups").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitGroupsAfterPatch = modelInputPatched.get("visitGroups").size();

        assertThat(numberOfVisitGroupsAfterPatch).isEqualTo(numberOfVisitGroups + 1);

        JsonNode addedVisitGroup = modelInputPatched.get("visitGroups").get(numberOfVisitGroupsAfterPatch - 1);
        assertThat(addedVisitGroup.get("id").asText()).isEqualTo("ce9aa5ex");
        assertThat(addedVisitGroup.get("visits").size()).isEqualTo(1);
    }

    @Test
    public void testAddVisitToVisitGroup() throws IOException {

        String patch = """
                [
                          { "op": "add", "path": "/visitGroups/[id=ce9aa5e4]/visits/-", "value": {
                    "id": "0d7f6e5x",
                    "name": "Mick Robinson 1 / 3",
                    "location": [
                        33.80118537771744,
                        -84.40335014628359
                    ],
                    "timeWindows": [
                        {
                            "minStartTime": "2025-08-18T08:00:00-04:00",
                            "maxEndTime": "2025-08-18T16:00:00-04:00"
                        }
                    ],
                    "serviceDuration": "PT1H30M",
                    "requiredSkills": [
                        {
                            "name": "plumber",
                            "minLevel": null
                        }
                    ],
                    "priority": "8",
                    "pinningRequested": false
                }
                }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisitGroups = modelInput.get("visitGroups").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitGroupsAfterPatch = modelInputPatched.get("visitGroups").size();

        assertThat(numberOfVisitGroupsAfterPatch).isEqualTo(numberOfVisitGroups);

        JsonNode visitGroup = modelInputPatched.get("visitGroups").get(0);
        assertThat(visitGroup.get("id").asText()).isEqualTo("ce9aa5e4");
        assertThat(visitGroup.get("visits").size()).isEqualTo(4);
        assertThat(visitGroup.get("visits").get(3).get("id").asText()).isEqualTo("0d7f6e5x");
        assertThat(visitGroup.get("visits").get(3).get("name").asText()).isEqualTo("Mick Robinson 1 / 3");
    }

    @Test
    public void testReplaceVisitFromVisitGroup() throws IOException {

        String patch = """
                [
                          { "op": "replace", "path": "/visitGroups/[id=ce9aa5e4]/visits/[id=0d7f6e52]", "value": {
                    "id": "0d7f6e5x",
                    "name": "Mick Robinson 1 / 3",
                    "location": [
                        33.80118537771744,
                        -84.40335014628359
                    ],
                    "timeWindows": [
                        {
                            "minStartTime": "2025-08-18T08:00:00-04:00",
                            "maxEndTime": "2025-08-18T16:00:00-04:00"
                        }
                    ],
                    "serviceDuration": "PT1H30M",
                    "requiredSkills": [
                        {
                            "name": "plumber",
                            "minLevel": null
                        }
                    ],
                    "priority": "8",
                    "pinningRequested": false
                }
                }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisitGroups = modelInput.get("visitGroups").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitGroupsAfterPatch = modelInputPatched.get("visitGroups").size();

        assertThat(numberOfVisitGroupsAfterPatch).isEqualTo(numberOfVisitGroups);

        JsonNode visitGroup = modelInputPatched.get("visitGroups").get(0);
        assertThat(visitGroup.get("id").asText()).isEqualTo("ce9aa5e4");
        assertThat(visitGroup.get("visits").size()).isEqualTo(3);
        assertThat(visitGroup.get("visits").get(2).get("id").asText()).isEqualTo("0d7f6e5x");
        assertThat(visitGroup.get("visits").get(2).get("name").asText()).isEqualTo("Mick Robinson 1 / 3");
    }

    @Test
    public void testRemoveVisitFromVisitGroup() throws IOException {

        String patch = """
                [
                          { "op": "remove", "path": "/visitGroups/[id=ce9aa5e4]/visits/[id=0d7f6e52]" }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisitGroups = modelInput.get("visitGroups").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitGroupsAfterPatch = modelInputPatched.get("visitGroups").size();

        assertThat(numberOfVisitGroupsAfterPatch).isEqualTo(numberOfVisitGroups);

        JsonNode visitGroup = modelInputPatched.get("visitGroups").get(0);
        assertThat(visitGroup.get("id").asText()).isEqualTo("ce9aa5e4");
        assertThat(visitGroup.get("visits").size()).isEqualTo(2);
    }

    @Test
    public void testRemoveSkill() throws IOException {

        String patch = """
                [
                          { "op": "remove", "path": "/skills/[electrician]" }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisitGroups = modelInput.get("visitGroups").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitGroupsAfterPatch = modelInputPatched.get("visitGroups").size();

        assertThat(numberOfVisitGroupsAfterPatch).isEqualTo(numberOfVisitGroups);

        JsonNode skills = modelInputPatched.get("skills");
        assertThat(skills.size()).isEqualTo(3);
    }

    @Test
    public void testAddSkill() throws IOException {

        String patch = """
                [
                          { "op": "add", "path": "/skills/-", "value": "musician" }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        int numberOfVisitGroups = modelInput.get("visitGroups").size();

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        int numberOfVisitGroupsAfterPatch = modelInputPatched.get("visitGroups").size();

        assertThat(numberOfVisitGroupsAfterPatch).isEqualTo(numberOfVisitGroups);

        JsonNode skills = modelInputPatched.get("skills");
        assertThat(skills.size()).isEqualTo(5);
    }

    @Test
    public void testAddFreezeDeparturesBeforeTime() throws IOException {

        String patch = """
                [
                      { "op": "add", "path": "/freezeDeparturesBeforeTime", "value": "2027-02-01T14:10:00Z" }
                ]
                """;
        JsonNode modelInput = mapper
                .readTree(this.getClass()
                        .getResourceAsStream("/ai/timefold/solver/service/json/internal/patch/VISIT_GROUP_input.json"));

        JsonNode modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInput);

        JsonNode freezeDeparturesBeforeTime = modelInputPatched.get("freezeDeparturesBeforeTime");
        assertThat(freezeDeparturesBeforeTime.textValue()).isEqualTo("2027-02-01T14:10:00Z");

        patch = """
                [
                      { "op": "remove", "path": "/freezeDeparturesBeforeTime" }
                ]
                """;
        modelInputPatched = JsonPatch.apply((ArrayNode) mapper.readTree(patch), modelInputPatched);

        assertThat(modelInputPatched.get("freezeDeparturesBeforeTime")).isNull();
    }
}
