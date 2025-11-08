package de.seuhd.campuscoffee;

import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.PosType;
import de.seuhd.campuscoffee.domain.model.CampusType;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Service for importing a POS from an OSM XML node.
 */
@Service
public class PosOsmImportService {

    public Pos importFromOsmXmlNode(InputStream xmlNodeStream) throws Exception {
        Element nodeElement = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlNodeStream)
                .getDocumentElement();

        Long id = nodeElement.hasAttribute("id")
                ? Long.valueOf(nodeElement.getAttribute("id"))
                : null;

        Map<String, String> tagMap = new HashMap<>();
        NodeList tagNodes = nodeElement.getElementsByTagName("tag");
        for (int i = 0; i < tagNodes.getLength(); i++) {
            Element tagElement = (Element) tagNodes.item(i);
            tagMap.put(tagElement.getAttribute("k"), tagElement.getAttribute("v"));
        }

        // Required fields (use empty string or guess if missing)
        String name = tagMap.getOrDefault("name", "");
        String description = tagMap.getOrDefault("description", "");
        String street = tagMap.getOrDefault("addr:street", "");
        String houseNumber = tagMap.getOrDefault("addr:housenumber", "");

        // Integer postalCode
        String postalCodeStr = tagMap.get("addr:postcode");
        Integer postalCode;
        try {
            postalCode = postalCodeStr != null && !postalCodeStr.isBlank() ? Integer.valueOf(postalCodeStr) : 0;
        } catch (NumberFormatException e) {
            postalCode = 0;
        }

        String city = tagMap.getOrDefault("addr:city", "");

        // PosType (fallback to a valid type if not found—assuming CAFE)
        String amenity = tagMap.get("amenity");
        PosType posType = mapAmenityToPosType(amenity);
        if (posType == null) posType = PosType.CAFE; // fallback!

        // CampusType (fallback to ALTSTADT)
        CampusType campusType = CampusType.ALTSTADT;

        LocalDateTime dateTime = LocalDateTime.now();

        return new Pos(
                id,
                dateTime,
                dateTime,
                name,
                description,
                posType,
                campusType,
                street,
                houseNumber,
                postalCode,
                city
        );
    }

    private PosType mapAmenityToPosType(String amenity) {
        if (amenity == null) return null;
        switch (amenity) {
            case "cafe": return PosType.CAFE;
            case "bakery": return PosType.BAKERY;
            case "cafeteria": return PosType.CAFETERIA;
            case "vending_machine": return PosType.VENDING_MACHINE;
            default: return null;
        }
    }
}