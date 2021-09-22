package tech.kronicle.service.repofinders.github.models.api;

import lombok.Value;

@Value
public class UserRepo {

  String clone_url;
  String contents_url;
}
