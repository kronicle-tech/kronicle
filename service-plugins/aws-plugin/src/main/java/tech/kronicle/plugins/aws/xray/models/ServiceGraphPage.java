package tech.kronicle.plugins.aws.xray.models;

import lombok.Value;

import java.util.List;

@Value
public class ServiceGraphPage {

    List<Service> services;
    String nextPage;
}
