package net.travale.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import net.travale.model.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Converter
public class RolesSetToStringConverter implements AttributeConverter<Set<Role>, String> {

  @Override
  public String convertToDatabaseColumn(Set<Role> set) {
    if (set == null) {
      return "";
    }
    return set.stream().map(Enum::toString).collect(Collectors.joining(","));
  }

  @Override
  public Set<Role> convertToEntityAttribute(String joined) {
    HashSet<Role> myHashSet = new HashSet<>();
    if (joined == null || joined.isEmpty()) {
      return myHashSet;
    }
    StringTokenizer st = new StringTokenizer(joined, ",");
    while(st.hasMoreTokens())
      myHashSet.add(Role.valueOf(st.nextToken()));

    return myHashSet;
  }


}