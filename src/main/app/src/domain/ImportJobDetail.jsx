/*
 * MIT License
 *
 * Copyright (c) 2023 Staatsbibliothek zu Berlin - Preußischer Kulturbesitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import React from "react";
import JobDetails from "./JobDetails";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import {Box} from "@material-ui/core";
import {withRouter} from "react-router-dom";
import {JobDetailPrefix} from "../css/JobDetailPrefix"
import {ApplicationConfigurationContext} from "../infrastructure/ApplicationConfigurationContext";
import {Helmet} from "react-helmet";

class ImportJobDetail extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      data: [],
    }
  }

  componentDidMount() {
    this.fetchData(this.props.match.params.id)
  }

  componentWillUnmount() {
    this.setState({data: []})
  }

  async fetchData(id) {

    await window.fetch(this.context.backendBasePath + "/rest/import/job/" + id,
        {
          method: 'GET',
          headers: {},
        }).then(response => {
      return response.json();
    }).then((data) => {
      this.setState({
        data: data.importFiles.map(
            (r) => new JobDetails(r.id, r.path, r.dateiTyp,
                r.dateiFormat, "true", r.error, r.message))
      });
    }).catch(function (error) {
      console.log("Error occurred during upload")
      console.log(error)
    });
  }

  render() {

    return (<div><Helmet>
      <title>Handschriftenportal | Import Job Detailansicht</title>
      <meta name="description"
            content="Detailsansicht Ihres XML Import Vorgangs."/>
    </Helmet>
      {this.state.data.map((job) => {
        return (<Card variant="outlined" key={job.id}>
          <CardContent>
            <Typography variant="h5" component="h2">
              Import Job Details
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix component="span">ID:</JobDetailPrefix> <Box
                component="span">{job.id}</Box>
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix
                  component="span"> NAME:</JobDetailPrefix> {job.name}
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix component="span">TYP:</JobDetailPrefix> {job.typ}
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix
                  component="span">FORMAT:</JobDetailPrefix> {job.format}
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix
                  component="span">VALIDE:</JobDetailPrefix> {job.valide ? 'Ja'
                : 'Nein'}
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix
                  component="span">ERGEBNIS:</JobDetailPrefix> {job.ergebnis}
            </Typography>
            <Typography color="textSecondary"
                        gutterBottom>
              <JobDetailPrefix
                  component="span">FEHLER:</JobDetailPrefix> {job.fehler}
            </Typography>
          </CardContent>
        </Card>);
      })}
    </div>)
  }
}

ImportJobDetail.contextType = ApplicationConfigurationContext;

export default withRouter(ImportJobDetail);