import React, {Profiler, Suspense} from 'react';
import ReactDOM from 'react-dom'
import './App.css';
import ErrorBoundary from './infrastructure/ErrorBoundary'
import Header from './Header'
import {
  authentication,
  AuthenticationContext
} from './infrastructure/AuthenticationContext';
import CssBaseline from '@material-ui/core/CssBaseline';
import Box from "@material-ui/core/Box";
import ImportJob from "./domain/ImportJob";
import {FormattedMessage, IntlProvider} from "react-intl";
import de from './i18n/messages/DE-de.json'
import en from './i18n/messages/EN-en.json'
import Navigation from "./Navigation";

import UploadFileTable from './domain/UploadFileTable';
import UploadForm from './domain/UploadForm';
import {ApplicationConfigurationContext} from "./infrastructure/ApplicationConfigurationContext";
import ErrorMessage from "./infrastructure/ErrorMessage";
import {Helmet} from "react-helmet"

if (process.env.NODE_ENV !== 'production') {
  var axe = require('react-axe');
  axe(React, ReactDOM, 1000);
}

class Import extends React.Component {

  constructor(props) {
    super(props);
    this.state = {tableData: [], locale: 'DE-de', errorElement: null}

    this.fetchData = this.fetchData.bind(this);
    this.onLocaleChange = this.onLocaleChange.bind(this)
    this.setError = this.setError.bind(this)
  }

  componentDidMount() {
    this.fetchData();
  }

  fetchData() {

    window.fetch(this.context.backendBasePath + "/rest/import/job", {
      method: 'GET',
      headers: {},
    }).then(response => {
      return response.json();
    }).then((data) => {
      this.setState({
        tableData: data.map(
            (f) => new ImportJob(f.id, f.creationDate, f.benutzerName,
                f.importFiles.length, f.importDir, ""))
      });
    }).catch(this.setError)
  }

  setError(message) {
    console.log(message)
    this.setState({errorElement: ErrorMessage(message)});
  }

  onRenderImportCallback(
      id, // the "id" prop of the Profiler tree that has just committed
      phase, // either "mount" (if the tree just mounted) or "update" (if it re-rendered)
      actualDuration, // time spent rendering the committed update
      baseDuration, // estimated time to render the entire subtree without memoization
      startTime, // when React began rendering this update
      commitTime, // when React committed this update
      interactions // the Set of interactions belonging to this update
  ) {
    console.log("Import Modul has been rendered " + baseDuration)
  }

  onLocaleChange(locale) {
    this.setState({locale: locale})
  }

  render() {

    const browserLanguage = navigator.language.toLowerCase();
    console.log("Browser Language: " + browserLanguage)
    const messages = {'DE-de': de, 'EN-en': en}

    return (
        <div>
          <Helmet>
            <title>Handschriftenportal | XML Import</title>
            <meta name="description"
                  content="XML Import Service Handschriftenportal Staatsbibliothek zu Berlin. Hier können Sie Ihre Daten in Form von XML hochladen."/>
          </Helmet>
          <AuthenticationContext.Provider value={authentication}>
            <IntlProvider locale={this.state.locale}
                          messages={messages[this.state.locale]}
                          defaultLocale={this.state.locale}>
              <ErrorBoundary>
                <main className="App">
                  <Navigation onLocaleChange={this.onLocaleChange}></Navigation>
                  <Header title={<FormattedMessage id="header.titel"/>}/>

                  <Profiler id="Navigation"
                            onRender={this.onRenderImportCallback}>
                    <Suspense fallback={<div>Loading Upload File Table</div>}>
                      <React.Fragment>
                        <CssBaseline/>
                        <Box m={2}>
                          <UploadForm id="uploadform"
                                      tableUpdate={this.fetchData}
                                      endpoint={this.context.backendBasePath
                                      + "/rest/import"}>
                          </UploadForm>
                        </Box>
                        <Box m={2}>
                          {!this.state.errorElement ? (
                              <UploadFileTable data={this.state.tableData}
                                               headlines={['Datum', 'Benutzer',
                                                 'Anzahl Dateien',
                                                 'Name', 'Ergebnis']}>
                              </UploadFileTable>) : this.state.errorElement
                          }
                        </Box>
                      </React.Fragment>
                    </Suspense>
                  </Profiler>
                </main>
              </ErrorBoundary>
            </IntlProvider>
          </AuthenticationContext.Provider>
        </div>
    );

  }
}

Import.contextType = ApplicationConfigurationContext;

export default Import;
