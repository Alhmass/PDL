
import axios from 'axios'

export const getImages = async () => {
    try {
        const response = await axios.get('images')
        return response.data
    } catch (error) {
        console.error(error);
    }
}

export const getImage = async (url: string) => {
    try {
        const response = await axios.get(url, { responseType: "blob" })
        return response.data
    }
    catch (error) {
        console.error(error)
    }
}

export const putImage = async (file: string) => {
    try {
        let formdata = new FormData()
        formdata.append('file', file)
        await axios.post('/images', formdata, { headers: { 'Content-Type': 'multipart/form-data' } })
    } catch (error) {
        console.error(error)
    }
}