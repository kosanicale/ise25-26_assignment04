package de.seuhd.campuscoffee;

import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.PosType;
import de.seuhd.campuscoffee.domain.model.CampusType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PosOsmImportServiceTest {

    @Test
    void testImportFromOsmXmlNode_minimal() throws Exception {
        String xml = "<node id='12345'><tag k='name' v='Campus Bakery'/><tag k='amenity' v='bakery'/>"
                + "<tag k='addr:street' v='Example Street'/><tag k='addr:housenumber' v='13A'/>"
                + "<tag k='addr:postcode' v='12345'/><tag k='addr:city' v='Heidelberg'/></node>";
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        PosOsmImportService service = new PosOsmImportService();
        Pos pos = service.importFromOsmXmlNode(input);

        assertThat(pos).isNotNull();
        assertThat(pos.id()).isEqualTo(12345L);
        assertThat(pos.name()).isEqualTo("Campus Bakery");
        assertThat(pos.type()).isEqualTo(PosType.BAKERY);
        assertThat(pos.street()).isEqualTo("Example Street");
        assertThat(pos.houseNumber()).isEqualTo("13A");
        assertThat(pos.postalCode()).isEqualTo(12345);
        assertThat(pos.city()).isEqualTo("Heidelberg");
    }
}