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
