import configparser
import os
from dataclasses import dataclass

import streamlit

class AppConfig:

    nvidia_config: dict
    imdg_server_config: dict
    milvus_config: dict

    def __init__(self):
        self._load_nvidia_config()
        self._load_imdg_server_config()
        self._load_milvus_config()
        self._validate()

    def _load_nvidia_config(self):
        api_key = ""
        if streamlit.secrets["nvidia"]["api-key"]:
            api_key = streamlit.secrets["nvidia"]["api-key"]
        elif os.environ.get("NVIDIA_API_KEY", None):
            api_key = os.environ.get("NVIDIA_API_KEY", None)
        self.nvidia_config = {
            "api_key": api_key
        }

    def _load_imdg_server_config(self):
        imdg_server_url = ""
        if streamlit.secrets["imdg-server"]["url"]:
            imdg_server_url = streamlit.secrets["imdg-server"]["url"]
        elif os.environ.get("IMDG_SERVER_URL", None):
            imdg_server_url = os.environ.get("IMDG_SERVER_URL", None)
        self.imdg_server_config = {
            "url": imdg_server_url
        }

    def _load_milvus_config(self):
        milvus_uri = None
        if streamlit.secrets["milvus"]["uri"]:
            milvus_uri = streamlit.secrets["milvus"]["uri"]
        elif os.environ.get("MILVUS_URI", None):
            milvus_uri = os.environ.get("MILVUS_URI", None)

        user = None
        if streamlit.secrets["milvus"]["user"]:
            user = streamlit.secrets["milvus"]["user"]
        elif os.environ.get("MILVUS_USER", None):
            user = os.environ.get("MILVUS_USER", None)

        password = None
        if streamlit.secrets["milvus"]["password"]:
            password = streamlit.secrets["milvus"]["password"]
        elif os.environ.get("MILVUS_PASSWORD", None):
            password = os.environ.get("MILVUS_PASSWORD", None)

        self.milvus_config = {
            "uri": milvus_uri,
            "user": user,
            "password": password
        }

    def _validate(self):

        nvidia_api_key = self.nvidia_config["api_key"]
        imdg_server_url = self.imdg_server_config["url"]
        milvus_uri = self.milvus_config["uri"]
        milvus_user = self.milvus_config["user"]
        milvus_password = self.milvus_config["password"]

        if not nvidia_api_key:
            raise Exception(
                "NVIDIA API KEY not provided, either by secrets.toml - [nvidia][api-key] or environment variable NVIDIA_API_KEY")
        if not imdg_server_url:
            raise Exception(
                "IMDG server url not provided, either by secrets.toml - [imdg-server][url] or environment variable IMDG_SERVER_URL")
        if not milvus_uri:
            raise Exception(
                "Milvus uri not provided, either by secrets.toml - [milvus][uri] or environment variable MILVUS_URI")
        if not milvus_user:
            raise Exception(
                "Milvus user not provided, either by secrets.toml - [milvus][user] or environment variable MILVUS_USER")
        if not milvus_password:
            raise Exception(
                "Milvus password not provided, either by secrets.toml - [milvus][password] or environment variable MILVUS_PASSWORD")

    def get_nvidia_api_key(self):
        return self.nvidia_config["api_key"]

    def get_imdg_server_url(self):
        return self.imdg_server_config["url"]

    def get_milvus_uri(self):
        return self.milvus_config["uri"]

    def get_milvus_user(self):
        return self.milvus_config["user"]

    def get_milvus_password(self):
        return self.milvus_config["password"]

app_config = AppConfig()