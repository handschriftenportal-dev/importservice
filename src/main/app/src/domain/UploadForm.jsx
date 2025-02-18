import React from "react";
import {AuthenticationContext} from '../infrastructure/AuthenticationContext'
import {
  Button,
  ButtonGroup,
  Grid,
  Hidden,
  Input,
  InputLabel,
  Typography
} from '@material-ui/core';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import Box from "@material-ui/core/Box";
import IconButton from "@material-ui/core/IconButton";
import ArrowUpwardIcon from '@material-ui/icons/ArrowUpward';
import TextField from "@material-ui/core/TextField";
import {FormattedMessage} from "react-intl";

class UploadForm extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      title: "XML Datei Upload",
      datei: React.createRef(),
      invalid: true,
      dateiKey: new Date(),
      apiEndpoint: this.props.endpoint,
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleOnChange = this.handleOnChange.bind(this)
    this.handleOnChangeForReset = this.handleOnChangeForReset.bind(this)
  }

  handleOnChange(event) {
    if (event.target.value) {
      this.setState({invalid: false})
    }
  }

  handleOnChangeForReset(event) {

    console.log("Reset Form")
    this.setState({datei: React.createRef(), invalid: true})
  }

  async handleSubmit(event) {

    event.preventDefault();

    if (!this.state.datei.current || !this.state.datei.current.files[0]) {
      alert("Bitte Datei auswählen.")

      this.state.datei.current.focus();
    } else {
      console.log(
          'Starting upload of file ' + this.state.datei.current.files[0].name
          + " to API " + this.state.apiEndpoint);
      console.log(this.state.datei.current.files[0])

      await this.upload(this.state.datei.current.files[0])

      this.setState(
          {datei: React.createRef(), invalid: true, dateiKey: new Date()})

      this.props.tableUpdate();
    }
  }

  async upload(file) {

    const formData = new FormData();

    formData.append('datei', file);

    await window.fetch(this.state.apiEndpoint, {
      method: 'POST',
      headers: {'Accept-Encoding': 'gzip, deflate'},
      body: formData,
    }).then((response) => {
      console.log("Response of uploading file")
      console.log(response)
    }).catch(function (error) {
      console.log("Error occurred during upload")
      console.log(error)
    });
  }

  componentDidMount() {
    console.log("Mount UploadForm")
  }

  componentWillUnmount() {
    console.log("Umount UploadForm")
  }

  render() {
    return <Grid container justify="center" alignItems="center"
                 direction="column">
      <Hidden>
        <Grid item md={1}></Grid>
      </Hidden>
      <Grid item xs={12} md={10}>
        <Typography
            id="uploadheadline" variant="h5"
            component="h6">{<FormattedMessage id="form.welcome"/>}
          {' ' + this.context.username}</Typography>
        <form id="uploadform" onSubmit={this.handleSubmit} title="uploadform"
              aria-invalid={this.state.invalid} aria-label={'uploadform'}>
          <Box m={2}>
            <Box component="div" visibility="hidden">
              <InputLabel htmlFor="uploadFile"
                          aria-label={'uploadfilelabel'}>{this.state.title}</InputLabel>
            </Box>
            <IconButton
                variant="contained" aria-label="button"
                component="label" style={{width: "300px"}}
            ><ArrowUpwardIcon
                aria-label="button">UpArrowButton</ArrowUpwardIcon>
              <Input key={this.state.dateiKey} type="file"
                     name="datei"
                     onChange={this.handleOnChange}
                     placeholder=""
                     style={{display: "none"}}
                     id="uploadFile" inputRef={this.state.datei}
                     aria-label="uploadinputformular"/>
              <TextField id="standard-basic" variant="outlined" fullWidth
                         label={!this.state.datei.current
                         || !this.state.datei.current.files[0] ? ''
                             : this.state.datei.current.files[0].name}/>
            </IconButton>
          </Box>
          <Box m={2}>
            <div>
              <ButtonGroup aria-label="outlined secondary button group">
                <Button id="submitupload" aria-label="hinzufügen"
                        type="submit"
                        variant="outlined"
                        color="primary"
                        startIcon={<CloudUploadIcon/>}>
                  <FormattedMessage id="form.hinzufuegen"/>
                </Button>
                <Button variant="outlined" color="secondary" type="reset"
                        aria-label="zurücksetzen"
                        onClick={this.handleOnChangeForReset}>
                  <FormattedMessage id="form.zuruecksetzen"/>
                </Button>
              </ButtonGroup>
            </div>
          </Box>
        </form>
      </Grid>
      <Hidden>
        <Grid item md={1}></Grid>
      </Hidden>
    </Grid>
  }
}

UploadForm.contextType = AuthenticationContext;

export default UploadForm
