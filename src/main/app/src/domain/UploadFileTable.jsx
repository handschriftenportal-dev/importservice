import React, {useContext, useEffect, useState} from "react";
import {
  Grid,
  Hidden,
  InputLabel,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
} from "@material-ui/core";
import {Link} from "react-router-dom";
import {AuthenticationContext} from "../infrastructure/AuthenticationContext";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";

export default function UploadFileTable(props) {

  const [filter, setFilter] = useState('');
  const authentication = useContext(AuthenticationContext)

  useEffect(() => {
    console.log("Rendering UploadFileTabled finished ...")
    return () => {
      console.log("Unmount UploadFileTable");
      setFilter('')
    }
  }, [])

  function onChange(e) {
    setFilter(e.currentTarget.value)
  }

  return (<Grid container justify="center" direction="column"
                alignItems="center">
    <Hidden>
      <Grid item md={1}></Grid>
    </Hidden>
    <Grid container item xs={12} md={10} alignItems="stretch"
          direction="column">
      <Typography component="h3" variant="subtitle1">{'Angemeldeter Benutzer: '
      + authentication.username}</Typography>
      <Paper>
        <Box component="div" visibility="hidden">
          <InputLabel htmlFor="dateinameinput">Dateieingabe</InputLabel>
        </Box>
        <TextField id="dateinameinput" label={'Dateiname filtern'}
                   value={filter}
                   onChange={onChange}></TextField>
        <Table>
          <TableHead>
            <TableRow>
              {props.headlines.map(l => {
                return <TableCell scope="col" key={l}>{l}</TableCell>
              })}
            </TableRow>
          </TableHead>
          <TableBody>
            {
              props.data[0] ? (props.data.filter(
                  job => job.dateiname.toLowerCase().includes(
                      filter.toLowerCase())).map((importJob) => {
                return (
                    <TableRow key={importJob.id} className="uploadfilerow">
                      <TableCell><Link to={'/app/import/job/'
                      + importJob.id}>{importJob.datum}</Link></TableCell>
                      <TableCell>{importJob.benutzer}</TableCell>
                      <TableCell>{importJob.dateianzahl}</TableCell>
                      <TableCell>{importJob.dateiname}</TableCell>
                      <TableCell>{importJob.ergebnis}</TableCell>
                    </TableRow>)
              })) : (<TableRow><TableCell>Keine Daten
                vorhanden</TableCell></TableRow>)
            }
          </TableBody>
        </Table>
      </Paper>
    </Grid>
    <Hidden>
      <Grid item md={1}></Grid>
    </Hidden>
  </Grid>)
}
