package no.ngu.dspace;

import junit.framework.TestCase;
import no.ngu.dspace.model.*;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DSpaceClientTester  extends TestCase {
    private final String endPoint = "https://localhost:8080/rest/";
    private final String username = "changeThisBeforeRunningTest";
    private final String password = "changeThisBeforeRunningTest";

    public void testRestAvailable() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.isRestAvailable();
        assertTrue(dSpaceClient.isRestAvailable());
    }
    public void testLogin() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        Status status = dSpaceClient.getStatus();
        assertTrue(status.isAuthenticated());
    }
    public void testGetCommunities() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        List<Community> communities = dSpaceClient.getCommunities();
        assertFalse(communities.isEmpty());
        System.out.println(String.format("Found %s communities",communities.size()));
    }
    public void testGetCollections() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        List<Collection> collections = dSpaceClient.getCollections();
        for (Collection collection:collections) {
            System.out.println(String.format("%s: %s", collection.getUuid(), collection.getName()));
        }
        assertFalse(collections.isEmpty());
        System.out.println(String.format("Found %s collections",collections.size()));
    }
    /*
    public void testGetItemsInCollection() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        String collectionUuid = "b003ea0c-5e3a-4258-8d30-d2f411546156"; // ¨Test-samling i Delarkiv for Test
        Collection collection = new Collection();
        collection.setId(collectionUuid);
        List<Item> items = dSpaceClient.getItemsByCollection(collection);
        assertTrue(items.size() > 0);
    }

     */

    public void testFindItemByAuthor() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        MetadataEntry entry = new MetadataEntry();
        entry.setKey("dc.contributor.author");
        entry.setValue("Riber, Knut");
        // Filter on own instead? dc.description.localcode=<mylocalId>
        List<Item> items = dSpaceClient.findItemByMetadata(entry.getKey(),entry.getValue());
        assertFalse(items.isEmpty());
        System.out.println(String.format("Filter (%s=%s) returned %s objects",entry.getKey(),entry.getValue(),items.size()));
    }

    public void testGetItemMetadataByUuid() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        String uuid = "b9b90d5f-6b50-455b-9334-ca0c4184d2ed"; // Kvartærgeologisk kart, (dc.contributor.author=Riber, Knut)
        Item item = new Item();
        item.setUuid(uuid);
        List<MetadataEntry> entries = dSpaceClient.getItemMetadata(item);
        assertFalse(entries.isEmpty());
    }

    public void testFlushSamlingForTest() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        String collectionUuid = "b003ea0c-5e3a-4258-8d30-d2f411546156"; // ¨Test-samling i Delarkiv for Test
        Collection collection = new Collection();
        collection.setUuid(collectionUuid);
        List<Item> items = dSpaceClient.getItemsByCollection(collection);
        int teller=1;
        for (Item item: items) {
            boolean deleted = dSpaceClient.deleteItem(item,collection);
            System.out.println(String.format("Deleting item %s/%s",teller,items.size()));
            teller++;
        }
        assertTrue(teller>= items.size());
    }

    public void testFullAddUpdateDeleteItemAndBitstream() throws Exception {
        DSpaceClient dSpaceClient = new DSpaceClient(endPoint,username,password);
        dSpaceClient.login();
        // To add an object to a collection, we need first an Item, a Collection, and later add Metadata to the Item.
        Item item = new Item();
        String name = "Test-rapport";
        item.setName(name);
        Collection collection = new Collection();
        // Change this: You need a test collection e.g. "Collection for test"
        collection.setUuid("b003ea0c-5e3a-4258-8d30-d2f411546156");

        // Create Item in Collection in an Archive
        Item addedItem = dSpaceClient.addItem(item,collection);
        /* Verify object was written to DSpace by verifying we got an Uuid and link in return */
        assertNotNull(addedItem.getUuid());
        assertFalse(addedItem.getUuid().isEmpty());
        assertNotNull(addedItem.getLink());

        // Metadata fields for Item - using Dublin Core Schema (dc.*)
        List<MetadataEntry> metadataEntries = new ArrayList<MetadataEntry>();
        metadataEntries.add(new MetadataEntry("dc.contributor.author","Doe, John","en_US"));
        metadataEntries.add(new MetadataEntry("dc.title","Testing title","en_US"));
        metadataEntries.add(new MetadataEntry("dc.description","random description","en_US"));
        metadataEntries.add(new MetadataEntry("dc.description.abstract","short description","en_US"));

        //assertEquals(name,addedItem.getName());
        //assertEquals(item.getName(),addedItem.getName());
        // Add metadata to Item
        boolean addedItemMetadata = dSpaceClient.addItemMetadata(addedItem,metadataEntries);
        assertTrue(addedItemMetadata);
        /* Read back metadata from REST */
        List<MetadataEntry> metadataAdded = dSpaceClient.getItemMetadata(addedItem);
        assertFalse(metadataAdded.isEmpty());
        assertTrue(metadataAdded.size() >= metadataEntries.size());
        System.out.println(String.format("Found %s metadata entries. Required (at least): %s", metadataAdded.size(),metadataEntries.size()));
        // Update metadata
        List<MetadataEntry> updateMetadataEntries = new ArrayList<MetadataEntry>();
        String _key = "dc.description";
        String _value = "Updated description";
        updateMetadataEntries.add(new MetadataEntry(_key,_value));
        boolean updatedItemMetadata = dSpaceClient.updateItemMetadata(addedItem,updateMetadataEntries);
        assertTrue(updatedItemMetadata);
        // Read back updated metadata
        List<MetadataEntry> metadataUpdated = dSpaceClient.getItemMetadata(addedItem);
        boolean foundUpdatedValue = false;
        for (MetadataEntry entry:metadataUpdated) {
            if (entry.getKey().equals(_key)) {
                System.out.println("Found matching key: " + entry.getKey());
                if (entry.getValue().equals(_value)) {
                    foundUpdatedValue = true;
                }
            }
        }
        assertTrue(foundUpdatedValue);
        // Add attachments aka Bitstreams
        String filename = "/IdeaProjects/dspace-client/src/test/resources/test.pdf";
        String FILE_URL = "https://www.ngu.no/upload/Publikasjoner/Kart/KV10/Nesset_Eikesdalen_KV10.pdf";
        String FILE_NAME = FILE_URL.substring(FILE_URL.lastIndexOf("/")+1);
        InputStream in = new URL(FILE_URL).openStream();
        Files.copy(in, Paths.get(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
        //File uploadFile = new File(filename);
        File uploadFile = new File(FILE_NAME);
        // https://github.com/DSpace/DSpace/blob/master/dspace-rest/src/main/java/org/dspace/rest/ItemsResource.java
        Bitstream bitstream = new Bitstream();
        bitstream.setMimeType("application/pdf");
        bitstream.setSizeBytes((int) uploadFile.length());
        bitstream.setFormat("PDF");
        bitstream.setDescription("PDF-report");
        bitstream.setName(uploadFile.getName());
        System.out.println("Add " + uploadFile.getName() + " to bitstream.");
        Bitstream createdBitstream = dSpaceClient.createBitstream(uploadFile,bitstream,addedItem);
        assertFalse(createdBitstream.getUuid().isEmpty());
        assertFalse(createdBitstream.getRetrieveLink().isEmpty()); // Make shure we have a downloadable link
        String md5sum = checksumFile(uploadFile,"MD5");
        assertEquals(md5sum,createdBitstream.getCheckSum().getValue().toUpperCase());

        boolean deletedBitstream = dSpaceClient.deleteBitstream(createdBitstream,addedItem);
        assertTrue(deletedBitstream);
        // Delete item/metadata
        System.out.println(String.format("Deleting Item (%s) in Collection (%s)",addedItem.getUuid(),collection.getUuid()));
        boolean deleted = dSpaceClient.deleteItem(addedItem,collection);
        assertTrue(deleted);
        boolean deletedCachedFile = uploadFile.delete();
        assertTrue(deletedCachedFile);

    }

    private String checksumFile(File file, String hashDigest) throws NoSuchAlgorithmException,IOException {
        String filename = file.getAbsolutePath();
        MessageDigest md = MessageDigest.getInstance(hashDigest.toUpperCase());
        md.update(Files.readAllBytes(Paths.get(filename)));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}
