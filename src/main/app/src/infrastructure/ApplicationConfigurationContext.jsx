import * as React from "react";
import prod from '../application-production.json'
import dev from '../application-development.json'
import stage from '../application-stage.json'
import test from '../application-test.json'

export var configuration;

if (process.env.NODE_ENV === 'production') {
  configuration = prod;
}

if (process.env.NODE_ENV === 'development') {
  configuration = dev;
}

if (process.env.NODE_ENV === 'stage') {
  configuration = stage;
}

if (process.env.NODE_ENV === 'test') {
  configuration = test;
}

if (!configuration) {
  configuration = dev;
}

export const ApplicationConfigurationContext = React.createContext(
    configuration
);
