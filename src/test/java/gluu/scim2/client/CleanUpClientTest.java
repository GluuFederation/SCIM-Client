package gluu.scim2.client;

import java.util.Arrays;
import java.util.List;

import org.gluu.site.ldap.persistence.exception.EntryPersistenceException;
import org.jboss.seam.Component;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.util.StringHelper;
import org.gluu.oxtrust.ldap.service.GroupService;
import org.gluu.oxtrust.model.GluuGroup;
import org.gluu.oxtrust.model.scim2.Group;

/**
 * @author Yuriy Movchan
 * @version 0.1, 03/24/2014
 */

public class CleanUpClientTest extends gluu.BaseComponentTest {

		@Override
		public void beforeClass() {
		}

		@Override
		public void afterClass() {
		}

		@Test
		@Parameters(value = "usedGroups")
		public void cleanUpClient(String usedGroups) {
			Assert.assertNotNull(usedGroups);
			List<String> usedGroupList = Arrays.asList(StringHelper.split(usedGroups, ",", true, false));
			output("Used groups: " + usedGroupList);

			final GroupService groupService = (GroupService) Component.getInstance(GroupService.class);
			
			int groupsResultSetSize = 50;

			int countResults = 0;
			int countRemoved = 0;
			boolean existsMoreGroups = true;
			while (existsMoreGroups && countResults < 10000) {
				List<GluuGroup> gluuGroups = groupService.getAllGroups();

				existsMoreGroups = gluuGroups.size() == groupsResultSetSize;
				countResults += gluuGroups.size();
		
				Assert.assertNotNull(gluuGroups);
				output("Found groups: " + gluuGroups.size());
				output("Total groups: " + countResults);
		
				for (GluuGroup Group : gluuGroups) {
					String groupId = Group.getInum();
					if (!usedGroupList.contains(groupId)) {
						try {
							groupService.removeGroup(Group);
						} catch (EntryPersistenceException ex) {
							output("Failed to remove group: " + ex.getMessage());
						}
						countRemoved++;
					}
				}
			}

			output("Removed groups: " + countRemoved);
		}
	}
