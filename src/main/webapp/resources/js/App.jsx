import { Goto, Upload, DonwloadFileURL } from "./Api";


const App = () => {
    const [curdir, setCurdir] = React.useState(["root"])
    const [files, setFiles] = React.useState([])

    // Display the files in current directory at the beginning
    React.useEffect(() => {
        Goto(curdir).then((data) => {
            setFiles(data)
        })
    }, [])

    // Refresh the file list when the directory is changed
    function refreshFileList(dir) {
        setCurdir(dir)
        // call the API to get the files in the directory
        Goto(dir).then((data) => {
            setFiles(data)
        })
    }

    // Display the file server page
    return (
        <>
            <div className="container">
                <PageHeader message="File Server" />
                <DropArea curdir={curdir} refreshFileList={refreshFileList} />
                <PathHeader curdir={curdir} refreshFileList={refreshFileList} />
                <Table files={files} curdir={curdir} refreshFileList={refreshFileList} />
            </div>
        </>
    );
}

export default App;

const PageHeader = ({ message }) => {
    return (
        <>
            <div className="page-header">
                <h1>{message}</h1>
            </div>
        </>
    )
}

const DropAreaDiv = styled.div`
    border: 1px dashed lightblue;
    height: 50px;
`

const ActiveDropAreaDiv = styled.div`
    border: 1px solid goldenrod;
    height: 50px;
`

const DropArea = ({ curdir, refreshFileList }) => {
    const [dropActive, setDropActive] = React.useState(false)
    const [displayMsg, setDisplayMsg] = React.useState("drop your file here to upload");

    const dragHandler = (ev, active) => {
        ev.preventDefault();
        setDropActive(active)
    }

    const dropHandler = (ev) => {
        ev.preventDefault();

        // get the file item from the event
        var file_item = getFileItem(ev)

        // handle the upload failure result
        const handleFailure = (error) => {
            setDisplayMsg('failed to upload file: ' + file_item.name + ' to "/' + curdir.join('/') + '"')
            if (error) {
                console.log(error)
            }
        }
        // handle the upload success result
        const handleSuccess = () => {
            setDisplayMsg('uploaded file: ' + file_item.name + ' to "/' + curdir.join('/') + '"')
            refreshFileList(curdir)
        }

        if (file_item) {
            // call the upload API top upload the file
            Upload(file_item, curdir)
                .then((upload_filesize) => {
                    if (upload_filesize == null || upload_filesize == 0) {
                        handleFailure()
                        return
                    }
                    handleSuccess()
                })
                .catch((error) => {
                    handleFailure(error)
                })
        }

        clearDropData(ev)
        setDropActive(false);
    }

    const upload_box = (
        <>
            <div
                style={{ height: "100%" }}
                onDragOver={(ev) => dragHandler(ev, true)}
                onDragLeave={(ev) => dragHandler(ev, false)}
                onDrop={(ev) => dropHandler(ev)}
            >
                {displayMsg}
            </div>
        </>
    )

    // display the drop area with different style when the drop is active
    return (
        <>
            {dropActive &&
                <ActiveDropAreaDiv className="row">
                    {upload_box}
                </ActiveDropAreaDiv>
            }
            {!dropActive &&
                <DropAreaDiv className="row">
                    {upload_box}
                </DropAreaDiv>
            }
        </>
    )
}

const PathHeader = ({ curdir, refreshFileList }) => {
    // sequentially create the path items
    var prev_dir = [];
    const path_items = curdir.map((dir_name, index) => {
        var upto_dir = {
            "dir": prev_dir.concat([dir_name]),
            "name": dir_name
        };
        prev_dir.push(dir_name);
        return <PathItem key={index} path={upto_dir} refreshFileList={refreshFileList} />
    });
    return (
        <>
            <div className="row" id="pathheader">
                <h3>Current Directory: {path_items}</h3>
            </div>
        </>
    )
}

const PathItem = ({ path, refreshFileList }) => {
    const showFiles = () => {
        refreshFileList(path.dir)
    };

    return (
        <>
            <span> / <a onClick={showFiles} href="#"> {path.name} </a> </span>
        </>
    )
}

const Table = ({ files, curdir, refreshFileList }) => {
    files.sort(compareFileLastModified);

    return (
        <>
            <table className="row table">
                <tbody id="filelist">
                    <tr>
                        <th>File Name</th>
                        <th>Size</th>
                        <th>Last Modified</th>
                    </tr>
                    {files.map((f, index) => {
                        if (f.isFolder) {
                            return <FolderItem key={index} file={f} curdir={curdir} refreshFileList={refreshFileList} />
                        } else {
                            return <FileItem key={index} file={f} curdir={curdir} />
                        }
                    })}
                </tbody>
            </table>
        </>
    )
}

const FolderItem = ({ file, curdir, refreshFileList }) => {
    const showFilesInThisFolder = () => {
        var ndirs = curdir.concat([file.fileName]);
        refreshFileList(ndirs)
    };
    var lmdate = new Date(file.lastModified);
    return (
        <tr>
            <td>
                <a onClick={showFilesInThisFolder} href="#"> {file.fileName} </a> /
            </td>
            <td>{file.length}</td>
            <td>{lmdate.toLocaleString()}</td>
        </tr>
    )
}

const FileItem = ({ file, curdir }) => {
    var dllink = DonwloadFileURL(curdir, file.fileName)
    var flm = new Date(file.lastModified);
    return (
        <tr>
            <td><a href={dllink}> {file.fileName} </a></td>
            <td>{file.length}</td>
            <td>{flm.toLocaleString()}</td>
        </tr>
    )
}

// Get the file item from the event
function getFileItem(ev) {
    if (ev.dataTransfer.items && ev.dataTransfer.items.length > 0) {
        var file_item = ev.dataTransfer.items[0];
        if (file_item.kind === 'file') {
            return file_item.getAsFile();
        }
    }
    return null;
}

// Clear the drag data from the event
function clearDropData(ev) {
    if (ev.dataTransfer.items) {
        // Use DataTransferItemList interface to remove the drag data
        ev.dataTransfer.items.clear();
    } else {
        // Use DataTransfer interface to remove the drag data
        ev.dataTransfer.clearData();
    }
}

// Compare the files based on the last modified date. Folder first, then files.
function compareFileLastModified(f1, f2) {
    if (f1.isFolder && f2.isFolder) {
        return f1.lastModified - f2.lastModified;
    } else if (f1.isFolder && !f2.isFolder) {
        return -1;
    } else if (f2.isFolder && !f1.isFolder) {
        return 1;
    } else {
        return f2.lastModified - f1.lastModified;
    }
}