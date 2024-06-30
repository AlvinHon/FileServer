class UploadBox extends React.Component {
    render() {
        let message = this.props.message ? this.props.message : "drop your file here to upload";
        return <div style={{ height: "100%" }} onDragOver={this.props.dragOverHandler} onDrop={this.props.dropHandler} >
            {message}
        </div>
    }
}

class PathItem extends React.Component {
    folder = () => {
        this.props.gotofunc(this.props.path.dir);
    };

    render() {
        return <span> / <a onClick={this.folder}> {this.props.path.name} </a> </span>
    }
}

class PathHeader extends React.Component {

    render() {
        var prevdir = [];
        const dirs = this.props.curdir.map((d) => {
            var curdir = {
                "dir": prevdir.concat([d]),
                "name": d
            };
            prevdir.push(d);
            return <PathItem gotofunc={this.props.gotofunc} path={curdir} />
        });
        return <h3>Current Directory: {dirs} </h3>;
    }
}

class FolderItem extends React.Component {
    folder = () => {
        var ndirs = this.props.curdir.concat([this.props.file.fileName]);
        this.props.gotofunc(ndirs);
    };
    render() {
        var lmdate = new Date(this.props.file.lastModified);
        return <tr>
            <td>
                <a onClick={this.folder} > {this.props.file.fileName} </a> /
            </td>
            <td>{this.props.file.length}</td>
            <td>{lmdate.toLocaleString()}</td>
        </tr>;
    }
}

class FileRecord extends React.Component {

    render() {
        var fls = this.props.files;
        fls.sort(function (f1, f2) {
            if (f1.isFolder && f2.isFolder) {
                return f1.lastModified - f2.lastModified;
            } else if (f1.isFolder && !f2.isFolder) {
                return -1;
            } else if (f2.isFolder && !f1.isFolder) {
                return 1;
            } else {
                return f1.lastModified - f2.lastModified;
            }
        });
        const ullist = fls.map((f) => {
            if (f.isFolder) {
                return <FolderItem gotofunc={this.props.gotofunc} curdir={this.props.curdir} file={f} />
            } else {
                var cdir = this.props.curdir.slice(1).join("/");
                var dllink = "/download?file=" + cdir + "/" + encodeURIComponent(f.fileName);
                var flm = new Date(f.lastModified);
                return <tr>
                    <td><a href={dllink}> {f.fileName} </a></td>
                    <td>{f.length}</td>
                    <td>{flm.toLocaleString()}</td>
                </tr>;
            }
        });
        return ullist;
    }
}
