/**
 * Fetch the data from the server
 * @param {*} paths file path
 * @returns List of files and directories
 */
export const Goto = async (paths) => {
    const response = await fetch(GotoURL(paths));
    if (!response.ok) {
        return null
    }
    let data = await response.json();
    return data
}

/**
 * Upload the file to the server
 * @param {*} file file to upload
 * @param {*} paths directory to upload
 * @returns size of the file uploaded
 */
export const Upload = async (file, paths) => {
    var fd = new FormData();
    fd.append('file', file);
    fd.append('path', paths.slice(1).join('/'));
    const response = await fetch('/upload', {
        method: 'POST',
        body: fd
    });
    if (!response.ok) {
        return null
    }
    let data = await response.json();
    return data
}

/**
 * The URL to fetch the list of files from the server
 * @param {*} paths file path
 * @returns URL to fetch the list of files
 */
export const GotoURL = (paths) => {
    var cdir = paths.slice(1).join("/");
    return "/goto?path=" + cdir
}

/**
 * The URL to download the file
 * @param {*} paths directory to download
 * @param {*} filename file name to download
 * @returns URL to download the file
 */
export const DonwloadFileURL = (paths, filename) => {
    var cdir = paths.slice(1).join("/");
    return "/download?file=" + cdir + "/" + encodeURIComponent(filename)
}