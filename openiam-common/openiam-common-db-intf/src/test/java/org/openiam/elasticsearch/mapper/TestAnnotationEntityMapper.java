package org.openiam.elasticsearch.mapper;

import java.io.IOException;

import org.openiam.elasticsearch.mapper.AnnotationEntityMapper;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.testng.Assert;
import org.testng.annotations.Test;


public class TestAnnotationEntityMapper {

	@Test
	public void testEmailMapping() throws IOException {
		final AnnotationEntityMapper mapper = new AnnotationEntityMapper();
		
		final EmailAddressEntity original = new EmailAddressEntity();
		original.setEmailAddress("foobar");
		original.setId("abcdefg");
		original.setParent(new UserEntity());
		original.getParent().setId("12345");
		
		final String stringRepresintation = mapper.mapToString(original);
		
		final EmailAddressEntity mapped = mapper.mapToObject(stringRepresintation, EmailAddressEntity.class);
		
		Assert.assertEquals(original, mapped);
		
		/* checks that a non-Field field is not serialized */
		original.setName("abslkdfj");
		Assert.assertNull(mapper.mapToObject(mapper.mapToString(original), EmailAddressEntity.class).getName());
	}
}
