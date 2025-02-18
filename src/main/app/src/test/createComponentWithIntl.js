import React from 'react';
import renderer from 'react-test-renderer';
import {IntlProvider} from 'react-intl';
import de from '../i18n/messages/DE-de.json'
import en from "../i18n/messages/EN-en.json";
import {BrowserRouter as Router} from 'react-router-dom';

const messages = {'DE-de': de, 'EN-en': en}

const createComponentWithIntl = (children, container) => {
  return renderer.create(<Router><IntlProvider locale={'DE-de'}
                                               messages={messages['DE-de']}
                                               defaultLocale={'DE-de'}>{children}</IntlProvider></Router>,
      container);
};

export default createComponentWithIntl;
